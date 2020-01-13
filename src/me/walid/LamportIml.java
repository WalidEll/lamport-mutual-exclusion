package me.walid;

import lamport.Lamport;
import lamport.LamportHelper;
import lamport.LamportPOA;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Arrays;
import java.util.Collections;

public class LamportIml extends LamportPOA {
    public static final String req="REQ";
    public static final String ack="ACK";
    public static final String rel="REL";

    private int localClock;
    private int[] allClocks=new int[3];
    private String[] allMessages=new String[3];
    private int[]neighbors;
    private int i;

    public LamportIml(int[] neighbors, int i) {
        this.neighbors = neighbors;
        this.i = i;

        Arrays.fill(allClocks, 0);
        Arrays.fill(allMessages, "");
    }

    @Override
    public void sendReq(int p, int clock) {
        try {
            org.omg.CORBA.Object objRef =   Server.orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            Lamport addobj = (Lamport) LamportHelper.narrow(ncRef.resolve_str("P"+p));
            System.out.println("Send REQ to P"+p);
            addobj.acceptReq(i,this.localClock);
        }
        catch (Exception e) {
            System.out.println("Hello Client exception: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void sendAck(int p, int clock) {
        try {
            org.omg.CORBA.Object objRef =  Server.orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            Lamport addobj = (Lamport) LamportHelper.narrow(ncRef.resolve_str("P"+p));
            System.out.println("Send ACK to P"+p);
            addobj.acceptAck(i,this.localClock);
        }
        catch (Exception e) {
            System.out.println("Lamport exception: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void sendRel(int p, int clock) {
        try {
            org.omg.CORBA.Object objRef =   Server.orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            Lamport addobj = (Lamport) LamportHelper.narrow(ncRef.resolve_str("P"+p));
            System.out.println("Send REL to P"+p);
            addobj.acceptRel(i,this.localClock);
        }
        catch (Exception e) {
            System.out.println("Lamport exception: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void acceptReq(int p, int clock) {
        this.localClock=Math.max(clock,this.localClock)+1;
        this.allClocks[p]=clock;
        this.allMessages[p]=req;
        sendAck(p,localClock);
    }

    @Override
    public void acceptAck(int p, int clock) {
        this.localClock=Math.max(clock,this.localClock)+1;
        if (!this.allMessages[p].equals(req)){
            this.allClocks[p]=clock;
            this.allMessages[p]=ack;
        }
    }

    @Override
    public void acceptRel(int p, int clock) {
        this.localClock=Math.max(clock,this.localClock)+1;
        this.allClocks[p]=clock;
        this.allMessages[p]=rel;
    }

    @Override
    public void criticalSessionRequest() {
        this.localClock++;
        for (int i=0;i<neighbors.length;i++){
            if (i!=this.i)
                sendReq(i,this.localClock);
        }
        this.allClocks[this.i]=this.localClock;
        this.allMessages[this.i]=req;
        //wait
        while (true){
            boolean allowedToAccessCS=false;
            for (int j=0;j<neighbors.length;j++){
                if (j!=this.i){
                    if ( allClocks[this.i] < allClocks[j] || ((allClocks[this.i] == allClocks[j]) && i<j)){
                    allowedToAccessCS=true;
                }else {
                    allowedToAccessCS=false;
                    break;
                }
                }
            }
            if (allowedToAccessCS)
            //CS
            {
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
        this.criticalSessionRealise();
    }

    @Override
    public void criticalSessionRealise() {
        this.localClock++;
        for (int i=0;i<neighbors.length;i++){
            if (i!=this.i)
            sendRel(i,this.localClock);
        }
        this.allClocks[this.i]=this.localClock;
        this.allMessages[this.i]=rel;
    }

    public int getIdentifier() {
        return i;
    }
}
