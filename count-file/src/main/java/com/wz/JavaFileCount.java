package com.wz;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * 统计java文件个数
 * Created by wangzi on 2017-07-31.
 */
@Mojo(name = "count", defaultPhase = LifecyclePhase.PACKAGE)
public class JavaFileCount extends AbstractMojo{
    @Parameter(property = "path", defaultValue = ".")
    private String path;
    @Parameter(property = "suffix", defaultValue = "java")
    private String suffix;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info( "----------------- JavaFileCount   -----------------");
        getLog().info( "****** path   ****** : " + path);
        getLog().info( "****** suffix ****** : " + suffix );
        getLog().info( "****** count  ****** : " + countFile(new File(path)));
        getLog().info( "----------------- JavaFileCount   -----------------");
    }

    private long countFile(File root){
        long count = 0;
        if (root.isDirectory()){
            for (File file : root.listFiles()){
                if (file.isDirectory()){
                    count += countFile(file);
                }else if (file.isFile() && file.getName().endsWith(suffix)){
                    count++;
                }
            }
        }else if (root.isFile() && root.getName().endsWith(suffix)){
            count++;
        }
        return count;
    }

}
