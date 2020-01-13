package lamport;


/**
* lamport/LamportOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from lamport.idl
* Saturday, January 11, 2020 3:52:09 PM WEST
*/

public interface LamportOperations 
{
  void sendReq (int p, int clock);
  void sendAck (int p, int clock);
  void sendRel (int p, int clock);
  void acceptReq (int p, int clock);
  void acceptAck (int p, int clock);
  void acceptRel (int p, int clock);
  void criticalSessionRequest ();
  void criticalSessionRealise ();
} // interface LamportOperations
