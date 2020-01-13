package me.walid;

import lamport.Lamport;
import lamport.LamportHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;



public class Server  {

    static ORB orb;
    public static void main(String[] args) {
        try{
            // create and initialize the ORB
            //// get reference to rootpoa
            orb = ORB.init(args, null);
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            // activate the POAManager
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            int[]neighbors={0,1,2};
            LamportIml serverObj = new LamportIml(neighbors,Integer.valueOf(args[0]));

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(serverObj);
            Lamport href = LamportHelper.narrow(ref);
            // get the root naming context
            org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            // bind the Object Reference in Naming
            NameComponent path[] = ncRef.to_name( "P"+serverObj.getIdentifier());
            ncRef.rebind(path, href);

            System.out.println("Server P"+ args[0]+" ready and waiting ...");

            // wait for invocations from clients
            for (;;){
                orb.run();
            }
        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
        System.out.println("Server Exiting ...");
    }
}
