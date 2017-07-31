package com.wz;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import static sun.security.pkcs11.wrapper.Functions.toHexString;

/**
 * 静态资源版本更新
 * 对"/resources/static"目录下的资源进行控制，另外，使用以下格式对资源进行引用：
 * <script type='text/javascript' src='/resources/static/js/header.js' />
 * <link rel='stylesheet' type='text/css' href='/resources/css/default.css' />
 * Created by wangzi on 2017-07-31.
 */
@Mojo(name = "version", defaultPhase = LifecyclePhase.PACKAGE)
public class VersionControl extends AbstractMojo{
    @Parameter(property = "path", defaultValue = ".")
    private String path;
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info( "----------------- VersionControl   -----------------");
        getLog().info( "****** path   ****** : " + path);
        try {
            addVersion();
        }catch (Exception e){
        }
        getLog().info( "----------------- VersionControl   -----------------");
    }

    private void addVersion() throws Exception{
        List<File> list = getFiles(new File(path + "\\src\\main\\resources\\static"));
        for (File file : list){
            List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                for (File file1 : list){
                    if (line.contains(file1.getName())) { //内容没变，不用更新
                        if (line.contains(getHash(file1.getAbsolutePath()))){
                            continue;
                        }
                        lines.remove(i);
                        int index = line.lastIndexOf("?")>-1 ? line.lastIndexOf("?")+33 : line.lastIndexOf("'");
                        lines.add(i, line.substring(0, index) + "?" + getHash(file1.getAbsolutePath()) + line.substring(index));
                    }
                }
            }
            Files.write(Paths.get(file.getAbsolutePath()), lines);
        }
    }

    /**
     * 获取文件列表
     * @param root
     * @return
     */
    private List<File> getFiles(File root){
        List<File> list = new ArrayList<>();
        if (root.isDirectory()){
            for (File file : root.listFiles()){
                if (file.isDirectory()){
                    list.addAll(getFiles(file));
                }else if (file.isFile() && matchSuffix(file.getName())){
                    list.add(file);
                }
            }
        }else if (root.isFile() && matchSuffix(root.getName())){
            list.add(root);
        }
        return list;
    }

    private boolean matchSuffix(String name){
        if (name.endsWith("html") || name.endsWith("css") || name.endsWith("js")){
            return true;
        }
        return false;
    }

    /**
     * 获取文件的MD5值
     * @param fileName
     * @return
     * @throws Exception
     */
    private static String getHash(String fileName)  throws Exception{
        InputStream fis = new FileInputStream(fileName);
        byte buffer[] = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        for(int numRead = 0; (numRead = fis.read(buffer)) > 0;){
            md5.update(buffer, 0, numRead);
        }
        fis.close();
        return toHexString(md5.digest());
    }
}
