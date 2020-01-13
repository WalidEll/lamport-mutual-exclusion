package me.walid;

import lamport.Lamport;
import lamport.LamportHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class Client {
    public static void main(String[] args) {
        try {
            ORB orb = ORB.init(args, null);
            org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            Lamport lamportIml = (Lamport) LamportHelper.narrow(ncRef.resolve_str("P"+args[0]));
            System.out.println("Done.....");
            lamportIml.criticalSessionRequest();
        }
        catch (Exception e) {
            System.out.println("Client exception: " + e);
            e.printStackTrace();
        }
    }
}