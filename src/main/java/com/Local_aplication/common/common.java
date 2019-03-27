package com.Local_aplication.common;

import com.Local_aplication.managers.EC2Manager;

import java.util.List;

public class common {
    public static List<String> initialization_script(){
        List<String> script = EC2Manager.init_script();
        script.add("cd /home/ec2-user/");
//        installing git
        script.add("sudo yum -y install git");
//        installing maven
        script.add("sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo");
        script.add("sudo sed -i s/\\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo");
        script.add("sudo yum install -y apache-maven");
//        creating log file
        script.add("touch log");
        return script;
    }

    public static List<String> manager_script(){
        List<String> script = initialization_script();
//        cloning git repo
        script.add("git clone https://github.com/hod246/DS_Ass1.git");
        script.add("cd DS_Ass1/");
//        building maven project
        script.add("mvn package");
//        running the manager script
        script.add("java -jar target/Local-application-1.0-SNAPSHOT.jar");
    return script;
    }
}
