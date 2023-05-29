package cn.mrcsh.mcquery.Controller;

import cn.mrcsh.mcquery.Annotation.API;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Hashtable;

@RestController
@RequestMapping("/api")
@Slf4j
public class MinecraftQueryController {
    @API(name = "querymcserver")
    @GetMapping("/querymcserver")
    public Object queryMcServer(String ServerIP, String port) throws IOException {
        HashMap<String, String> map = checkSRV(ServerIP);
        if (map == null) {
            return getServerInfo(ServerIP, port == null ? 25565 : Integer.parseInt(port));
        } else {
            return getServerInfo(map.get("host"), Integer.parseInt(map.get("port")));
        }
    }

    public HashMap<String, String> checkSRV(String Address) {
        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        hashtable.put("java.naming.provider.url", "dns:");

        try {
            Attribute attribute = (new InitialDirContext(hashtable))
                    .getAttributes("_Minecraft._tcp." + Address,
                            new String[]{"SRV"})
                    .get("srv");
            if (attribute != null) {
                String[] re = attribute.get().toString().split(" ", 4);
                HashMap<String, String> map = new HashMap<>();
                map.put("host", re[3].substring(0, re[3].length() - 1));
                map.put("port", re[2]);
                return map;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    // ==============================================

    public static byte[] int2varint(int input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            if ((input & ~0x7F) == 0) {
                out.write(input);
                break;
            }

            out.write((input & 0x7F) | 0x80);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            input >>>= 7;
        }
        return out.toByteArray();
    }

    public static int readVarintFromStream(DataInputStream in) throws IOException {
        int value = 0;
        int length = 0;
        byte currentByte;

        while (true) {
            currentByte = in.readByte();

            value |= (currentByte & 0x7F) << (length * 7);

            length += 1;
            if (length > 5) {
                throw new RuntimeException("VarInt is too big");
            }

            if ((currentByte & 0x80) != 0x80) {
                break;
            }
        }
        return value;
    }

    // ==============================================

    public String getServerInfo(String address, int port) throws IOException {
        String ip = "127.0.0.1";
        short portLocal = 25565;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bOut);
        out.write(0x00);
        out.write(int2varint(755));
        out.write(int2varint(ip.length()));
        out.writeBytes(ip);
        out.writeShort(portLocal);
        out.write(int2varint(0x01));
        Socket socket = new Socket(address, port);
        DataOutputStream sOut = new DataOutputStream(socket.getOutputStream());
        sOut.write(int2varint(bOut.size()));
        sOut.write(bOut.toByteArray());
        sOut.writeByte(0x01);
        sOut.writeByte(0x00);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        readVarintFromStream(in);
        int FLAG = readVarintFromStream(in);
        int LENGTH = readVarintFromStream(in);
        byte[] data = new byte[LENGTH];
        in.readFully(data);
        System.out.println(FLAG);
        System.out.println(LENGTH);
        System.out.println(new String(data, StandardCharsets.UTF_8));
        return new String(data, StandardCharsets.UTF_8);
    }
}
