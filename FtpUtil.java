import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FtpUtil {
    public String hostName = "172.114.248.161";
    public Integer port = 21;
    public String userName = "JackWang";
    public String password = "r3344520";
    public FTPClient ftpClient = new FTPClient();

    public static void main(String[] args) throws IOException {
        String username;
        String password;

        FtpUtil Client = new FtpUtil();
        System.out.println("请先登录");

        Scanner sc = new Scanner(System.in);
        username = sc.nextLine();
        password = sc.nextLine();

        Client.userName = username;
        Client.password = password;

        boolean sucess =  Client.initFtpClient();
        boolean flag = true;
        String order;
        while (flag){
            Client.menu();
            order = sc.nextLine();
            switch (order){
                case "upload":
                    System.out.println("请输入当前目录的任一文件名");
                    String filename = sc.nextLine();
                    String filenamepath = "D:\\JavaCode\\JavaAll\\FTPGUI\\src\\"+filename;
                    FileInputStream inputStream =new FileInputStream(new File(filenamepath));
                    Client.uploadFile("/ftp",filename,inputStream);
                    break;
                case "deletefile":
                    System.out.println("请输入你要删除的文件名");
                    filename = sc.nextLine();
                    Client.deleteFile("/",filename);
                    break;
                case "download":

                    String down;
                    System.out.println("输入你要下载的当前目录的任一文件名：");
                    down  = sc.nextLine();
                    Client.downloadFile("/",down,"D:\\JavaCode\\JavaAll\\FTPGUI\\src\\");
                    break;
                case "makemkdir":
                    String name ;
                    System.out.println("请输入你要创建的文件夹名");
                    name =  sc.nextLine();
                    Client.CreateDirecroty(name);
                    break;
                default:
                    System.out.println("输入有误");
                    break;
            }
        }

    }

    public void menu(){
        System.out.println("上传文件 upload");
        System.out.println("下载文件 download");
        System.out.println("删除文件 deletefile");
        System.out.println("创建文件夹 makemkdir");
    }
    public boolean initFtpClient() {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
            System.out.println("conecting...ftp服务器：" + this.hostName + ":" + this.port);
            ftpClient.connect(hostName, port);//连接ftp服务器
            ftpClient.login(userName, password);//登录ftp服务器
            int replyCode = ftpClient.getReplyCode();//登录是否成功
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("connect failed...ftp服务器" + this.hostName + ":" + this.port);
                return false;
            }
            System.out.println("connect success...ftp服务器" + this.hostName + ":" + this.port);
//            findList();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void findList() throws IOException {
        FTPFile[] files = ftpClient.listFiles();
        for (FTPFile file : files) {
            System.out.println(file.getName());
        }
    }

    public boolean uploadFile(String path, String filename, InputStream input) {
        boolean success = false;
        initFtpClient();
         FTPClient ftp = ftpClient;
            int reply;
            reply = ftp.getReplyCode();
        try {
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            ftp.changeWorkingDirectory(path);
            success = ftp.storeFile(filename, input);
            System.out.println("上传成功");
            input.close();
            ftp.logout();
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    //改变目录路径
    public boolean changeWorkingDirectory(String directory) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
            if (flag) {
               System.out.println("进入文件夹" + directory + " 成功！");
            } else {
                flag = false;
               System.out.println("进入文件夹" + directory + " 失败！开始创建文件夹");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return flag;
    }

    //创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
    public boolean CreateDirecroty(String remote) throws IOException {
        boolean success = true;
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            String path = "";
            String paths = "";
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), StandardCharsets.ISO_8859_1);
                path = path + "/" + subDirectory;
                if (existFile(path)) {
                    changeWorkingDirectory(subDirectory);
                }
                if (!makeDirectory(subDirectory)) {
                    System.out.println("创建目录[" + subDirectory + "]失败");
                }

                paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }

    public boolean downloadFile(String pathname, String filename, String localpath) {
        boolean flag = false;
        OutputStream os = null;
        try {
            System.out.println("开始下载文件");
            initFtpClient();
            //切换FTP目录
            if (!ftpClient.changeWorkingDirectory(pathname)) {
                System.out.println("不存在该目录！");
                return flag;
            }
            FTPFile[] ftpFiles = ftpClient.listFiles();
            String ftpFileName = "";
            for (FTPFile file : ftpFiles) {
                if (filename.equals(file.getName())) {
                    ftpFileName = file.getName();
                    File filePath = new File(localpath);
                    if (!filePath.exists()) {
                        filePath.mkdir();
                    }
                    os = new FileOutputStream(new File(localpath + "/" + file.getName()));
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            if (ftpFileName.equals("")) {
                System.out.println("下载失败，文件未找到！");
                return flag;
            }
            ftpClient.logout();
            flag = true;
            System.out.println("下载文件成功");
        } catch (Exception e) {
            System.out.println("下载文件失败");
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    //判断ftp服务器文件是否存在
    public boolean existFile(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    //创建目录
    public boolean makeDirectory(String dir) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (flag) {
                System.out.println("创建文件夹" + dir + " 成功！");

            } else {
                System.out.println("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除文件
     *
     * @param pathname FTP服务器保存目录
     * @param filename 要删除的文件名称
     * @return
     */
    public boolean deleteFile(String pathname, String filename) {
        boolean flag = false;
        try {
            System.out.println("开始删除文件");
            initFtpClient();
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
            System.out.println("删除文件成功");
        } catch (Exception e) {
            System.out.println("删除文件失败");
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
}
