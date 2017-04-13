import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.Files;
import java.util.*;

public class Server {

	private static final int sPort = 8000;   //The server will be listening on this port number
	private static ArrayList<Handler> writers = new ArrayList<Handler>();
	public static void main(String[] args) throws Exception {
		System.out.println("The server is running."); 
        	ServerSocket listener = new ServerSocket(sPort);
		int clientNum = 1;
        	try {
            		while(true) {
                		new Handler(listener.accept(),clientNum).start();
				
				clientNum++;
            			}
        	} finally {
            		listener.close();
        	} 
 
    	}

	/**
     	* A handler thread class.  Handlers are spawned from the listening
     	* loop and are responsible for dealing with a single client's requests.
     	*/
    	private static class Handler extends Thread {
        	private String message;    //message received from the client
		private String uname;
        	private Socket connection;
        	private ObjectInputStream in;	//stream read from the socket
        	private ObjectOutputStream out;    //stream write to the socket
        	private int no;		//The index number of the client

        	public Handler(Socket connection, int no)
		{
            		this.connection = connection;
	    		this.no = no;
        	}

        	public void run() {
 		try{	
			String msg=null;
			String name,typeofcasting,typeofObject,rest,m;
			//initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			writers.add(this);
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			uname=(String)in.readObject();
			System.out.println(uname+ " is connected!");
			
			try{
				while(true)
				{	m="";
					//receive the message sent from the client
					message = (String)in.readObject();
					//show the message to the user
					String str[]=message.split(":");
					name=str[0];
					msg=str[1];
					String[] s=msg.split(" ",3);
					typeofcasting=s[0];
					typeofObject=s[1];
					rest = s[2];
					File f;
					System.out.println("Received message: " + msg + " from client " + uname);
					
					if(typeofcasting.equals("broadcast")&& typeofObject.equals("message"))
						{	m=rest.substring(1, rest.length() - 1);
							for (Handler h : writers)
							 {
                    						if(this.out!=h.out) 
								{	 h.out.writeObject("0");  		
							  		 h.out.writeObject("@" + name + ": " + m);
								}
			
                    					}
						}
					else 	
						if(typeofcasting.equals("broadcast")&& typeofObject.equals("file"))
						{	
						
		
							m=rest.substring(1, rest.length() - 1);
							String[] akk=m.split("/");
							String filename=akk[akk.length-1];
							f=new File(System.getProperty("user.dir")+"\\"+filename);
							byte[] content=Files.readAllBytes(f.toPath());
							for (Handler h : writers)
							 {
								if(this.out!=h.out)    	
								{	h.out.writeObject("1");
									h.out.writeObject(filename);
									h.out.writeObject(content);
								}
							 }
					
						}
					else 
						if(typeofcasting.equals("unicast")&& typeofObject.equals("message"))
						{	
							String st[]=rest.split(" ");
							for(int z=0;z<(st.length-1);z++)
							{m=m+" "+st[z];}
							m=m.substring(2, m.length() - 1);
					
							for (Handler h : writers)
							{ 
                    						if(st[st.length-1].equals(h.uname))    		
								{
									h.out.writeObject("0");
									h.out.writeObject("@" + name + ": " + m);
								}
			
                    					}
						}
					else 	if(typeofcasting.equals("unicast")&& typeofObject.equals("file"))
						{	
							String st[]=rest.split(" ");
							for(int z=0;z<(st.length-1);z++)
							{		
									m=m+" "+st[z];
							}
							m=m.substring(2, m.length() - 1);
							String[] akk=m.split("/");
							String filename=akk[akk.length-1];
							f=new File(System.getProperty("user.dir")+"\\"+filename);
							byte[] content=Files.readAllBytes(f.toPath());
							for (Handler h : writers)
							{ 
                    							if(st[st.length-1].equals(h.uname))    		
									{	h.out.writeObject("1");
										h.out.writeObject(filename);
										h.out.writeObject(content);
									}
	
                    					 }
						}
					else 	
						if(typeofcasting.equals("blockcast")&& typeofObject.equals("file"))
						{	
							String st[]=rest.split(" ");
							for(int z=0;z<(st.length-1);z++)
							{		
									m=m+" "+st[z];
							}
							m=m.substring(2, m.length() - 1);
							String[] akk=m.split("/");
							String filename=akk[akk.length-1];
							f=new File(System.getProperty("user.dir")+"\\"+filename);
							byte[] content=Files.readAllBytes(f.toPath());
							for (Handler h : writers)
							 { 
                    							if((!((h.uname).equals(st[st.length-1])))&& this.out!=h.out)    		
									{
										h.out.writeObject("1");
										h.out.writeObject(filename);
										h.out.writeObject(content);
									}
			
                    					  }
						}
					else 
						if(typeofcasting.equals("blockcast")&& typeofObject.equals("message"))
						{	
							String st[]=rest.split(" ");
							for(int z=0;z<(st.length-1);z++)
							{	
								m=m+" "+st[z];
							}
							m=m.substring(2, m.length() - 1);
							for (Handler h : writers)
							 { 
                    						if((!((h.uname).equals(st[st.length-1])))&& this.out!=h.out)		
								{
									h.out.writeObject("0");
									h.out.writeObject("@" + name + ": " + m);
			
                    						}
							 }
						}
					else 
						{
							this.out.writeObject("0");
							this.out.writeObject("enter a valid command");

					
						}
				}
		   	   }
		   	   catch(ClassNotFoundException classnot)
		   	   {
				System.err.println("Data received in unknown format");
				
		   	   }
			   catch (ArrayIndexOutOfBoundsException e)
			   {
				System.err.println("Data received in unknown format");
				
			   }
			   catch (EOFException e)
			   {
				System.err.println("Data received in unknown format");
				
			   }
		}
		catch(IOException ioException)
		{
			System.out.println("Disconnected with Client " + uname);
			
		} 
		catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				writers.remove(this);
				connection.close();
			}
			catch(IOException ioException)
			{
				System.out.println("Disconnected with Client " + uname);
				
				
			}
		}
}}}