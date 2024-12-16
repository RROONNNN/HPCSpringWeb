package com.example.HPCSpringWeb.Service;

import com.example.HPCSpringWeb.Entity.JobProperties;
import com.example.HPCSpringWeb.Entity.User;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@Scope(value="session",proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SshService {
    private static  String REMOTE_DIR= "/media/sf_Share/";//+user_name_ssh
    @Value("${IP_Server_HPC}")
    private  String host;
   private final User user;
    private  String admin = "root";
    private    String adminPassword = "root";
    @PostConstruct
    public void init() {
        // Initialization logic
        System.out.println("JobInfoService initialized");
    }
    @PreDestroy
    public void destroy() {
        // Cleanup logic
        System.out.println("JobInfoService destroyed");
    }
@Autowired
 public SshService(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("User not found in session");
        }
        this.user=user;

 }

    private String executeSshCommand_User(String command) throws IOException {

        StringBuilder output = new StringBuilder();
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user.getUser_name_ssh(), host, 22);
            System.out.println("user: "+user.getUser_name_ssh());
          //  Session session = jsch.getSession("thuan", host, 22);
            session.setPassword(user.getPassword_ssh());
            System.out.println("password: "+user.getPassword_ssh());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
    private String executeSshCommand_Admin(String command) throws IOException {
        StringBuilder output = new StringBuilder();
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(admin, host, 22);
            session.setPassword(adminPassword);

            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
    private  String fixPathOutPut(String path){

        return  path.substring(path.indexOf(':') + 1);
    }
    private  String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName; // No extension found
        }
        return fileName.substring(0, lastDotIndex);
    }

public void SubmitCppMpiJob(JobProperties jobProperties, MultipartFile file){
     System.out.println("HOST: "+host);
    MoveFileToUserFile(file.getOriginalFilename());//b1
    //get name file without extension
//String namePbsJob=getFileNameWithoutExtension(file.getOriginalFilename())+".pbs";
    try {
        String commandToExecute = "module load mpi \n mpic++ " + "calcPiMpi.cpp" + "\n" + "mpirun -np " + jobProperties.getNumOfChunks() * jobProperties.getNcpus() + " ./a.out";
     executeSshCommand_User(PBSJobScriptGenerator.Command_createPBSJobScript(jobProperties, commandToExecute));
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    String command="cd ~/jobs;qsub "+  jobProperties.getName() + ".pbs";
        try {
            String jobId= executeSshCommand_User(command);//b2 submit
System.out.println("jobId: "+jobId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
}

public void SubmitPythonJob(JobProperties jobProperties, MultipartFile file){
    MoveFileToUserFile(file.getOriginalFilename());//b1
    try {
        String commandToExecute = "python3 " + file.getOriginalFilename();
        executeSshCommand_User(PBSJobScriptGenerator.Command_createPBSJobScript(jobProperties, commandToExecute));
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    String command="cd ~/jobs;qsub "+  jobProperties.getName() + ".pbs";
    try {
        String jobId= executeSshCommand_User(command);//b2 submit
        System.out.println("jobId: "+jobId);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
    public void SubmitCppJob(JobProperties jobProperties, MultipartFile file){
        MoveFileToUserFile(file.getOriginalFilename());//b1
        try {
            String commandToExecute = "g++ " + file.getOriginalFilename();
            executeSshCommand_User(PBSJobScriptGenerator.Command_createPBSJobScript(jobProperties, commandToExecute));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String command="cd ~/jobs;qsub "+  jobProperties.getName() + ".pbs";
        try {
            String jobId= executeSshCommand_User(command);//b2 submit
            System.out.println("jobId: "+jobId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void MoveFileToUserFile(String fileName){
       String   remote_dir=REMOTE_DIR+user.getUser_name_ssh()+"/";

        String command = "mv " + REMOTE_DIR + fileName + " /export/home/"+this.user.getUser_name_ssh()+"/jobs/ ; chown "+this.user.getUser_name_ssh()+" /export/home/"+this.user.getUser_name_ssh()+"/jobs/"+fileName;
        System.out.println(command);
        try {
            this.executeSshCommand_Admin(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  int getNumCpuFree(){
        String command ="pbsnodes -a | grep -A 1 \"free\" | grep \"pcpus\" | awk '{sum += $3} END {print sum}'";
        System.out.println("getNumCpuFree command: "+command);
    try{
        String output=executeSshCommand_User(command);
        System.out.println("getNumCpuFree output: "+output);
        if (output==null || output.isEmpty()){
            return 0;
        }
        return Integer.parseInt(output);

    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
public String getMemAvailable(){
    String command ="pbsnodes -a | grep \"resources_available.mem\" | awk '{sum += $3} END {print sum / 1024 \" MB\"}'";
    try{
        String output=executeSshCommand_User(command);
        System.out.println("output: "+output);

        return output;

    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
//add a new user
    public void addUser() throws IOException {
        String command = "sudo useradd -m -s /bin/bash " + user.getUser_name_ssh() +
                " && echo '" + user.getUser_name_ssh() + ":" + user.getPassword_ssh() + "' | sudo chpasswd ;sudo usermod -aG vboxsf "+ user.getUser_name_ssh();
            String output=executeSshCommand_Admin(command);
            command="mkdir ~/jobs";
            output=executeSshCommand_User(command);
            System.out.println("output: "+output);

}


}
