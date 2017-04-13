import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Client {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	//ObjectInputStream in;          //stream read from the socket
	BufferedReader br;
	String message;                //message send to the server
	

	void run(String username)
	{	String name=username;
		try{
			//create a socket to connect to the server
			requestSocket = new Socket("localhost", 8000);
			System.out.println("Connected to localhost in port 8000");
			File dir=new File(name);
			if(!dir.exists())
				dir.mkdir();
			//initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			out.writeObject(name);
			//in = new ObjectInputStream(requestSocket.getInputStream());
			
			//get Input from standard input
			br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter a command as a string");
			new ClientHelper(requestSocket,name).start();
			while(true)
			{
				
				message = br.readLine();
				String[] a=message.split(" ");
				if(a.length<3)
					System.out.println("enter a valid command");
				else
					out.writeObject(name+":"+message);
			}
		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
		} 
		catch (SocketException e) 
		{	System.err.println("Connection lost");
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	
	//main method
	public static void main(String args[])
	{	String name=args[0];
		Client client = new Client();
		client.run(name);
	}
}

class ClientHelper extends Thread{
	ObjectInputStream in;
	Socket connection;
	String uname;
	public ClientHelper(Socket s,String username) throws IOException{
	connection=s;
	uname=username;
	
	}
	@Override
	public void run(){
		 
		try{ 
			
	 		in = new ObjectInputStream(connection.getInputStream());
			while(true)
			{String str=(String) in.readObject();
			if(str.equals("0"))
				{String m=(String)in.readObject();
			System.out.println(m);}
			else if(str.equals("1"))
				{
				String filename=(String)in.readObject();
				byte[] content=(byte[])in.readObject();
				
				Path path=Paths.get(System.getProperty("user.dir")+File.separator+uname+File.separator+filename);
				Files.write(path,content);
				System.out.println("file "+filename+ " received");
				}
			}
		}
		catch(Exception e){e.printStackTrace();}
		finally{ try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
	}}
	





