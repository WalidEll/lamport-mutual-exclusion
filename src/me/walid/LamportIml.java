package me.walid;

import lamport.Lamport;
import lamport.LamportHelper;
import lamport.LamportPOA;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Arrays;


public class LamportIml extends LamportPOA {
    public static final String req="REQ";
    public static final String ack="ACK";
    public static final String rel="REL";

    private int localClock;
    private int[] allClocks=new int[3];
    private String[] allMessages=new String[3];
    private int[]neighbors;
    private int i;
    private NamingContextExt ncRef;
    private boolean isAllowedToAccesCriticalSession=false;
    public LamportIml(int[] neighbors, int i) {
        this.neighbors = neighbors;
        this.i = i;

        Arrays.fill(allClocks, 0);
        Arrays.fill(allMessages, "");
        org.omg.CORBA.Object objRef = null;
        try {
            objRef = Server.orb.resolve_initial_references("NameService");
            ncRef = NamingContextExtHelper.narrow(objRef);
        } catch (InvalidName invalidName) {
            invalidName.printStackTrace();
        }
    }

    @Override
    public void sendReq(int p, int clock) {
        try {

            Lamport addobj = (Lamport) LamportHelper.narrow(ncRef.resolve_str("P"+p));
            System.out.println("Send REQ to P"+p+" With Clock: "+clock+" Local Clock: "+this.localClock);
            addobj.acceptReq(i,clock);
        }
        catch (Exception e) {
            System.out.println("Client exception: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void sendAck(int p, int clock) {
        try {
            Lamport addobj = (Lamport) LamportHelper.narrow(ncRef.resolve_str("P"+p));
            this.localClock++;
            System.out.println("Send ACK to P"+p+" With Clock: "+this.localClock);
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
            System.out.println("Send REL to P"+p+" With Clock: "+clock+" Local Clock: "+this.localClock);
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
        System.out.println("Accept REQ from P"+p+" With Clock: "+clock+" Local Clock: "+this.localClock);
        this.allClocks[p]=clock;
        this.allMessages[p]=req;
        sendAck(p,localClock);
    }

    @Override
    public void acceptAck(int p, int clock) {
        this.localClock=Math.max(clock,this.localClock)+1;
        System.out.println("Accept ACK from P"+p+" With Clock: "+clock+" Local Clock: "+this.localClock);
        if (!this.allMessages[p].equals(req)){
            this.allClocks[p]=clock;
            this.allMessages[p]=ack;
        }
    }

    @Override
    public void acceptRel(int p, int clock) {
        this.localClock=Math.max(clock,this.localClock)+1;
        System.out.println("Accept REL from P"+p+" With Clock: "+clock+" Local Clock: "+this.localClock);
        this.allClocks[p]=clock;
        this.allMessages[p]=rel;
    }

    @Override
    public void criticalSessionRequest() {
        this.localClock++;
        int currentLocalClock=this.localClock;
        this.allClocks[this.i]=this.localClock;
        this.allMessages[this.i]=req;
        for (int i=0;i<neighbors.length;i++){
            if (i!=this.i)
                sendReq(i,currentLocalClock);
        }
        //wait
        while (true){
            for (int j=0;j<neighbors.length;j++){
                System.out.println("All Clocks :["+allClocks[0]+","+allClocks[1]+","+allClocks[2]+"]");
                if (j!=this.i){
                    if ( allClocks[this.i] < allClocks[j] || ((allClocks[this.i] == allClocks[j]) && i<j)){
                        isAllowedToAccesCriticalSession=true;
                }else {
                        isAllowedToAccesCriticalSession=false;
                        break;
                }
                }
            }
            if (isAllowedToAccesCriticalSession)
            //CS
            {
                try {
                    System.out.println("P"+this.i+" is using Critical Session for 10 sec, Local Clock: "+this.localClock);
                    Thread.sleep(30 * 1000);
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        this.criticalSessionRealise();
    }

    @Override
    public void criticalSessionRealise() {
        isAllowedToAccesCriticalSession=false;
        this.localClock++;
        System.out.println("P"+this.i+" released Critical Session, Local Clock: "+this.localClock);
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
