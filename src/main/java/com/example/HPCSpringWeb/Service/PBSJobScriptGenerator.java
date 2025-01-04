package com.example.HPCSpringWeb.Service;

import com.example.HPCSpringWeb.Entity.JobProperties;
import com.example.HPCSpringWeb.Entity.User;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.*;

public class PBSJobScriptGenerator {
    public static String Command_createPBSJobScript(JobProperties jobProperties,String command){
        String scriptContent = "#!/bin/bash\n" +
                "#PBS -N " + jobProperties.getName() + "\n" +
                "#PBS -l nodes="+jobProperties.getNumOfChunks()+":ppn=" + jobProperties.getNcpus() + "\n" +
                "#PBS -l walltime=" + jobProperties.getWalltime() + "\n" +
                "#PBS -l mem=" + jobProperties.getMem() + "\n" +
                "#PBS -o vbox.local:/media/sf_Share/" + jobProperties.getOwner().getUser_name_ssh() + "/\n" +
                "#PBS -e vbox.local:/media/sf_Share/" + jobProperties.getOwner().getUser_name_ssh() + "/\n" +
                "\n" ;
        if(jobProperties.isMailNotification()){
            scriptContent += "#PBS -m abe\n" +
                    "#PBS -M " + jobProperties.getOwner().getEmail() + "\n";
        }
       scriptContent+=  "cd \\$PBS_O_WORKDIR \n" + command + "\n";
       // scriptContent+=   command + "\n";
        String filePath = "~/jobs/" + jobProperties.getName() + ".pbs";
        System.out.println("PBSJobScriptGenerator: " + filePath);
        return "echo \"" + scriptContent + "\" > " + filePath;
    }

//    public static void main(String[] args) {
//        JobProperties jobProperties = new JobProperties(new User(), "test", "00:01:00", 1, 1, "1gb", "tsarlvntn2004@gmail.com");
//        String command = "mpic++ " + "file.cpp" + "\n" + "mpirun -np " + jobProperties.getNumOfChunks() * jobProperties.getNcpus() + " ./a.out";
//        System.out.println(Command_createPBSJobScript(jobProperties, command));
//    }

}
