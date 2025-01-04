package com.example.HPCSpringWeb.Controller;

import com.example.HPCSpringWeb.Entity.JobProperties;
import com.example.HPCSpringWeb.Entity.JobTypes;
import com.example.HPCSpringWeb.Entity.User;
import com.example.HPCSpringWeb.Service.FileService;
import com.example.HPCSpringWeb.Service.SshService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {
    private static  String UPLOAD_DIR = "D:/nam3/PBL4/Share/";//LOCAL_SHARE_DIR
  //  private  final User user;
    private final SshService sshService;
//    private final String userName="thuan";
private User getUserFromSession(HttpSession session) {
    User user = (User) session.getAttribute("user");
    if (user == null) {
        throw new IllegalStateException("User not found in session");
    }
    return user;
}
    public MainController(SshService sshService) {
        this.sshService = sshService;
        System.out.println("UPLOAD_DIR Inconstructor: " + UPLOAD_DIR);
    }
    private static String getName(Authentication authentication) {
        return Optional.of(authentication.getPrincipal())
                .filter(OidcUser.class::isInstance)
                .map(OidcUser.class::cast)
                .map(OidcUser::getEmail)
                .orElse(authentication.getName());
    }

    @GetMapping({"/", "/home"})
    public String home(Model theModel ,@RequestParam(value = "typejob", required = false) String typejob,Authentication authentication, HttpSession session) {
        User user = getUserFromSession(session);
        String email = getName(authentication);
        List<String> filenames = null;
        try {
            String  upload_dir=  UPLOAD_DIR + user.getUser_name_ssh() + "/";
            File upoloadDir = new File(upload_dir);
            if (!upoloadDir.exists()) {
                upoloadDir.mkdir();
            }
            filenames = FileService.getAllFilenames(upload_dir);
            System.out.println("filenames: "+filenames);
theModel.addAttribute("user",user);
            theModel.addAttribute("numCpuFree", sshService.getNumCpuFree());
            theModel.addAttribute("memAvailable", sshService.getMemAvailable());
            theModel.addAttribute("filenames",filenames);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(typejob!=null){
            theModel.addAttribute("typejob",typejob);
        }
        return "home";
    }
    @PostMapping("/handleUploadFile")
    public String uploadMultipleFiles(
            @RequestParam("typejob") String typejob,
            @RequestParam("mainfile") MultipartFile mainFile,
            @RequestParam("enclosedfiles")  List<MultipartFile> enclosedfiles,
            @RequestParam("job-name") String jobName,
            @RequestParam("num-chunks") int numChunks,
            @RequestParam("num-CpuPerChunk") int numCpuPerChunk,
            @RequestParam("memory") String memory,
            @RequestParam("wall_time") String wallTime,
            @RequestParam(value = "is_gmail_notification", required = false) boolean isGmailNotification,
            RedirectAttributes redirectAttributes,
            HttpSession session){
        User user = getUserFromSession(session);
        if (mainFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/";
        }
        try {
        String  upload_dir=  UPLOAD_DIR + user.getUser_name_ssh() + "/";
        File upoloadDir = new File(upload_dir);
        if (!upoloadDir.exists()) {
            upoloadDir.mkdir();
        }
            JobProperties jobProperties=new JobProperties( user, jobName, wallTime, numChunks, numCpuPerChunk, memory, isGmailNotification);
            System.out.println("jobProperties: "+jobProperties);
            try {
        for(MultipartFile file: enclosedfiles) {
            if (file.isEmpty()) {
                continue; //next pls
            }
            Path path = Paths.get(upload_dir + file.getOriginalFilename());
            Files.write(path, file.getBytes());
        }
                Path path = Paths.get(upload_dir + mainFile.getOriginalFilename());
                Files.write(path, mainFile.getBytes());
                System.out.println("File uploaded successfully: " + mainFile.getOriginalFilename());
if (typejob==null||typejob.equals(JobTypes.cppmpijob.toString())){
                sshService.SubmitCppMpiJob(jobProperties,mainFile,enclosedfiles);}
                else if(typejob.equals(JobTypes.pythonjob.toString())){
                    sshService.SubmitPythonJob(jobProperties,mainFile,enclosedfiles);
                }
                else if(typejob.equals(JobTypes.cppjob.toString())){
                    System.out.println("SubmitCppJob");
                    sshService.SubmitCppJob(jobProperties,mainFile,enclosedfiles);
                }
                System.out.println("Submit successfully: " + mainFile.getOriginalFilename());
//                         List<String> filenames = FileService.getAllFilenames(upload_dir);
//                         System.out.println("filenames: "+filenames);
//                         redirectAttributes.addFlashAttribute("filenames",filenames);
            } catch (Exception e) {
                e.printStackTrace();
            }
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + mainFile.getOriginalFilename() + "!");
        } catch (RuntimeException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload " + mainFile.getOriginalFilename() + " => " + e.getMessage());
        }
        return "redirect:/";
    }
    @GetMapping("/downloadfile")
    public ResponseEntity<Resource> dowloadFile(@RequestParam("filename") String filename, HttpSession session) {
        User user = getUserFromSession(session);
        String  upload_dir=  UPLOAD_DIR + user.getUser_name_ssh() + "/";
        System.out.println("filename"+upload_dir + filename);
        File file = new File(upload_dir + filename);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Resource resource=new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        System.out.println("resource: "+resource);
        System.out.println("headers: "+headers);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
