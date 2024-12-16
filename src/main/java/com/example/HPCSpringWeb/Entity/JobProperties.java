package com.example.HPCSpringWeb.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Getter
@Setter
@AllArgsConstructor
public class JobProperties {
    private User owner;
private String name;
private String walltime;
private int numOfChunks;//num of nodes
private int ncpus;
private String mem;
private boolean  mailNotification;
    public JobProperties() {
        this.owner = new User();
        this.name = "test";
        this.walltime = "00:10:00";
        this.numOfChunks = 1;
        this.ncpus = 4;
        this.mem = "300mb";
        this.mailNotification = false;
    }
    @Override
    public String toString() {
        return  "JobProperties{" +
                "owner=" + owner +
                ", name='" + name + '\'' +
                ", walltime='" + walltime + '\'' +
                ", numOfChunks=" + numOfChunks +
                ", ncpus=" + ncpus +
                ", mem='" + mem + '\'' +
                ", mailNotification=" + mailNotification +
                '}';
    }
}
