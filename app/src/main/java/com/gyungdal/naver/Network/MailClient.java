package com.gyungdal.naver.Network;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

/**
 * Created by GyungDal on 2016-06-15.
 */
public class MailClient extends javax.mail.Authenticator{
    private static final String TAG = MailClient.class.getName();
    private String id, pass, host, t;
    private Store store;
    private Folder inbox;
    private static final String pattern = "=\\?(.*?)\\?(.*?)\\?(.*?)\\?=";

    public void setAccountDetails(String id,String pass, String host){
        this.id = id;
        this.pass = pass;
        this.host = host;
    }

    public int readMail(Date Start, Date End, String match, String now) {
        Properties props = System.getProperties();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "Project Assistant" + File.separator + now + File.separator;
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props);
        try {
            store = session.getStore("imaps");
            store.connect(this.host ,this.id, this.pass);
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.RECENT), true);
            Message msg[] = inbox.search(ft);
            for(Message message : msg){
                //지정한 날짜 이내에 보낸것이 아닐경우 그 다음 탐색
                if(!(message.getReceivedDate().after(Start)
                        && message.getReceivedDate().before(End)))
                    continue;
                Address[] frommsg = message.getFrom();
                //네이버와 구글에서 보낸것만 탐색
                if(!(frommsg[0].toString().contains("naver")))
                    continue;


                if(!isMatch(message.getSubject(), match))
                    continue;

                getClass(message.getSubject());
                getName(message.getSubject());
                getTeam(message.getSubject());
                Log.i("Received Date", message.getReceivedDate().toString());
                Object content = message.getContent();
                Object inmsg;
                if(getClass(message.getSubject()).matches("^[0-9]*$")
                        && getName(message.getSubject()).matches("^[0-9]*$")
                        && getTeam(message.getSubject()).matches("^[a-zA-Z]*$"))
                    t = path + getClass(message.getSubject()) + "반" + File.separator
                        + getTeam(message.getSubject()) + File.separator;
                else
                    t = path + "Unknown Name Roll" + File.separator;
                Log.i(TAG, t);
                List<String> files = new ArrayList<>();
                List<String> fileUrls = new ArrayList<>();
                if(content instanceof Multipart){
                    Multipart multi = ((Multipart)content);
                    for(int i = 0;i<multi.getCount();i++){
                        MimeBodyPart part = (MimeBodyPart)multi.getBodyPart(i);

                        if(Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())){
                            String fileName = getFileName(part.getFileName());
                            File file = new File(t);
                            if(!file.exists())
                                file.mkdirs();
                            file = new File(t + fileName);
                            part.saveFile(file);
                            Log.i("File", fileName);
                            Log.i("File URL" , part.getContent().toString());
                        }else{
                            inmsg = part.getContent();
                            if(inmsg instanceof javax.mail.internet.MimeMultipart){
                                MimeMultipart multipart = ((MimeMultipart) inmsg);
                                MimeBodyPart bodyPart = (MimeBodyPart)multipart.getBodyPart(0);
                                String inMsg = bodyPart.getContent().toString()
                                        .replaceAll("&gt;", "").replaceAll("&lt;", "");
                                Log.i("Mime MSG", inMsg);
                                File file = new File(t);
                                if(!file.exists())
                                    file.mkdirs();
                                if(t.contains("Unknown Name Roll"))
                                    file = new File(t + message.getSubject() + ".txt");
                                else
                                    file = new File(t + "body.txt");
                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(inMsg.getBytes());
                            }else
                                Log.i("MSG", inmsg.toString());
                                File file = new File(t);
                                if(!file.exists())
                                    file.mkdirs();
                                if(t.contains("Unknown Name Roll"))
                                    file = new File(t + message.getSubject() + ".txt");
                                else
                                    file = new File(t + "body.txt");
                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(inmsg.toString().getBytes());
                            //Log.i("MSG", getData(inmsg));
                        }
                    }
                    for(int i =0;i<files.size();i++){
                        Log.i("File Name", files.get(i));
                        Log.i("File Content", fileUrls.get(i));
                    }

                }else{
                    Log.i("Source", String.valueOf(message.getContent()));
                }
            }

            inbox.close(true);
            store.close();
        } catch(Exception e){
            Log.i("LIST", e.getMessage());
            return 1;
        }
        return 0;
    }


    private static String getFileName(String source) throws Exception {
        StringBuilder buffer = new StringBuilder();
        //2. default charset 지정

        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(source);
        //3. 내용 찾아서 decoding
        while (matcher.find()) {
            buffer.append(new String(Base64.decode(matcher.group(3), 0)));
        }
        //4. decoding할 게 없다면 그대로 반환
        if(buffer.toString().isEmpty()){
            buffer.append(source);
        }
        return buffer.toString();
    }

    @NonNull
    private static String getClass(String s){
        Log.i(TAG, "Mail Class : " + s.substring(s.indexOf("_")+2,s.indexOf("_")+3));
        return s.substring(s.indexOf("_")+2,s.indexOf("_")+3);
    }

    @NonNull
    private static String getName(String s){
        Log.i(TAG, "Mail Name : " + s.substring(s.indexOf("(")+1, s.lastIndexOf(")")));
        return s.substring(s.indexOf("(")+1, s.lastIndexOf(")"));
    }

    @NonNull
    private static String getTeam(String s){
        Log.i(TAG, "Mail Team : " + s.substring(s.indexOf("(")-1, s.indexOf("(")));
        return s.substring(s.indexOf("(")-1, s.indexOf("("));
    }

    private static boolean isMatch(String str, String match){
        if(match.contains("*")){
            return isMatch(str, match.substring(0, match.lastIndexOf("*")));
        }else
            return str.contains(match);
    }
}
