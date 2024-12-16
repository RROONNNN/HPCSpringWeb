package com.example.HPCSpringWeb.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class JobInfo {
    @JsonProperty("timestamp")
    private long timestamp;
    @JsonProperty("pbs_version")
    private String pbs_version;
    @JsonProperty("pbs_server")
    private String pbs_server;
    @JsonProperty("Jobs")
    private Map<String, Job> Jobs;

    public JobInfo() {}

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Job {
        @JsonProperty("Job_Name")
        private String Job_Name;
        @JsonProperty("Job_Owner")
        private String Job_Owner;
        @JsonProperty("resources_used")
        private ResourcesUsed resources_used;
        @JsonProperty("job_state")
        private String job_state;
        @JsonProperty("queue")
        private String queue;
        @JsonProperty("server")
        private String server;
        @JsonProperty("Checkpoint")
        private String Checkpoint;
        @JsonProperty("ctime")
        private String ctime;
        @JsonProperty("Error_Path")
        private String Error_Path;
        @JsonProperty("exec_host")
        private String exec_host;
        @JsonProperty("exec_vnode")
        private String exec_vnode;
        @JsonProperty("Hold_Types")
        private String Hold_Types;
        @JsonProperty("Join_Path")
        private String Join_Path;
        @JsonProperty("Keep_Files")
        private String Keep_Files;
        @JsonProperty("Mail_Points")
        private String Mail_Points;
        @JsonProperty("mtime")
        private String mtime;
        @JsonProperty("Output_Path")
        private String Output_Path;
        @JsonProperty("Priority")
        private int Priority;
        @JsonProperty("qtime")
        private String qtime;
        @JsonProperty("Rerunable")
        private boolean Rerunable;
        @JsonProperty("Resource_List")
        private ResourceList Resource_List;
        @JsonProperty("stime")
        private String stime;
        @JsonProperty("substate")
        private int substate;
        @JsonProperty("Variable_List")
        private VariableList Variable_List;
        @JsonProperty("euser")
        private String euser;
        @JsonProperty("egroup")
        private String egroup;
        @JsonProperty("queue_rank")
        private long queue_rank;
        @JsonProperty("queue_type")
        private String queue_type;
        @JsonProperty("comment")
        private String comment;
        @JsonProperty("etime")
        private String etime;
        @JsonProperty("run_count")
        private int run_count;
        @JsonProperty("Exit_status")
        private int Exit_status;
        @JsonProperty("Submit_arguments")
        private String Submit_arguments;
        @JsonProperty("project")
        private String project;

        public Job() {}

        @Getter
        @Setter
        @AllArgsConstructor
        public static class ResourcesUsed {
            @JsonProperty("cpupercent")
            private int cpupercent;
            @JsonProperty("cput")
            private String cput;
            @JsonProperty("mem")
            private String mem;
            @JsonProperty("ncpus")
            private int ncpus;
            @JsonProperty("vmem")
            private String vmem;
            @JsonProperty("walltime")
            private String walltime;

            public ResourcesUsed() {}
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class ResourceList {
            @JsonProperty("ncpus")
            private int ncpus;
            @JsonProperty("nodect")
            private int nodect;
            @JsonProperty("nodes")
            private String nodes;
            @JsonProperty("place")
            private String place;
            @JsonProperty("select")
            private String select;
            @JsonProperty("walltime")
            private String walltime;

            public ResourceList() {}
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class VariableList {
            @JsonProperty("PBS_O_HOME")
            private String pbsOHome;
            @JsonProperty("PBS_O_LANG")
            private String pbsOLang;
            @JsonProperty("PBS_O_LOGNAME")
            private String pbsOLogname;
            @JsonProperty("PBS_O_PATH")
            private String pbsOPath;
            @JsonProperty("PBS_O_MAIL")
            private String pbsOMail;
            @JsonProperty("PBS_O_SHELL")
            private String pbsOShell;
            @JsonProperty("PBS_O_WORKDIR")
            private String pbsOWorkdir;
            @JsonProperty("PBS_O_SYSTEM")
            private String pbsOSystem;
            @JsonProperty("PBS_O_QUEUE")
            private String pbsOQueue;
            @JsonProperty("PBS_O_HOST")
            private String pbsOHost;

            public VariableList() {}
        }
    }
}