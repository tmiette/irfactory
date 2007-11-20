/*

Copyright (c) 2006, 2007 David Lambert

This file is part of Factory.

Factory is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Factory is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Factory; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/middleware/FactoryConnection.java,v $
$Revision: 1.48 $
$Date: 2007/02/22 15:36:39 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.middleware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.client.components.LocalMessage;
import net.sourceforge.projectfactory.client.xml.ImportListXML;
import net.sourceforge.projectfactory.server.FactoryServer;
import net.sourceforge.projectfactory.server.actions.Action;
import net.sourceforge.projectfactory.server.actions.Recipient;
import net.sourceforge.projectfactory.server.actors.Actor;
import net.sourceforge.projectfactory.server.actors.EMail;
import net.sourceforge.projectfactory.server.actors.Server;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.Base64;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;
import net.sourceforge.projectfactory.xml.FactoryZipInput;
import net.sourceforge.projectfactory.xml.FactoryZipOutput;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Middleware.
 * Makes the link between servers and clients.
 * @author David Lambert
 */
public class FactoryConnection {

    /** Timeout for socket connections. */
    private static int TIMEOUT = 10000;
    
    /** Size of the buffer client to server. */	
    private static int C2SBUFFERSIZE = 8192;

    /** Size of the buffer server to client. */ 
    private static int S2CBUFFERSIZE = 16384;

	/** Encryption 8-byte Salt. */
	private byte[] SALT = {
		(byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
		(byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
	};

	/** Encryption Iteration count. */
	static private int ITERATIONCOUNT = 19;

	/** Encryption ENCRYPTION. */
	private static String ENCRYPTION = "PBEWithMD5AndDES";

	/** Local server. */
	static private FactoryServer localServer;

	/** Local session. */
	static private FactorySession localSession;
	
    /** Error queue. */
    private FactoryWriterXML errQueue;

    /** Output queue. */
    private FactoryWriterXML outputQueue;
    
    /**Socket managed by the socketClient. */
    private Socket socketClient;
	
	/** Buffer for client output. */
	private OutputStreamWriter outputClient;
	
	/** Buffer for client input. */
	private BufferedReader inputClient;

	/** Requires a replication with the server. */
	private boolean replication;
	
    /** List of clients (used for notifications). */
    public List<FrameMain> mainFrames = new ArrayList();

    /** List of sessions on the server. */
    private List<FactorySession> sessions = new ArrayList();

    /** Thread used during initialization phase. */
    private ThreadInit threadInit = new ThreadInit();
	
	/** Encryption cipher. */
	private Cipher ecipher;

	/** Decryption cipher. */
	private Cipher dcipher;
	
	/** Creates a local server. */
	protected FactoryServer createServer() {
		return new FactoryServer();
	}
	
    /** Executes a query toward the server. */
    public void query(FactoryWriterXML query, 
                      FactoryWriterXML answer) {
        if(socketClient != null) 
			queryRemote(query, answer);
        else 
			queryLocal(query, answer);
    }

    /** Executes a query toward the local server. */
    public void queryLocal(FactoryWriterXML query, 
                      FactoryWriterXML answer) {
        if(localServer != null)
			localServer.query(localSession, query, answer);
    }

	/** Shutdown client socket. */
	private void shutDownClient() {
		if(!isConnected()) 
			return;
		try {
			socketClient.shutdownOutput();
            socketClient.close();
		}
		catch(Exception e) {
			return;
		}
        finally {
            socketClient = null;
        }
	}

	/** Encrypts a string. */
	private static String encrypt(Cipher ecipher, String str) 
				throws javax.crypto.BadPaddingException, 
						javax.crypto.IllegalBlockSizeException,
						java.io.IOException {
		return ecipher == null || str.length() == 0 ? str :
			Base64.encodeBytes(ecipher.doFinal(str.getBytes("UTF-8")),
				Base64.DONT_BREAK_LINES);
	}
		
	/** Decrypts a string. */
	private static String decrypt(Cipher dcipher, String str)
				throws javax.crypto.BadPaddingException, 
						javax.crypto.IllegalBlockSizeException,
						java.io.IOException {
		return dcipher == null || str.length() == 0 ? str :
			new String(dcipher.doFinal(Base64.decode(str)), "UTF-8");
	}

	/** Executes a query toward the remote server. */
    private void queryRemote(FactoryWriterXML query, 
                      FactoryWriterXML answer) {
		synchronized (this) {
			try {
				// Sends query to the server
				outputClient.write(encrypt(ecipher, 
                                            query.getOutWriter().toString()) + 
											"\r\n");
				outputClient.write("<end:request/>\r\n");
				outputClient.flush();

				// Read results from server
				String line;
				String buffer = "";
				while ((line = inputClient.readLine()) != null) {
					if(line.startsWith("<end:answer/>")) {
						String answerString = decrypt(dcipher, buffer);
						answer.copyFrom(answerString);
						return;
					}
					else if(line.startsWith("<end:messages/>")) {
						String messages = decrypt(dcipher, buffer);
						answer.copyErrorFrom(messages);
						buffer = "";
					}
					else 
						buffer += line;
				}
                answer.xmlMessage(FactoryWriterXML.ERROR, "server:error:client");
			} catch (SocketException e) {
			    answer.xmlMessage(FactoryWriterXML.ERROR, "server:error:client");
				shutDownClient();
			} catch (SocketTimeoutException e) {
			    answer.xmlMessage(FactoryWriterXML.ERROR, "server:timeout:client");
				shutDownClient();
			} catch (javax.crypto.BadPaddingException e) {
			    answer.xmlMessage(FactoryWriterXML.ERROR, "server:error:encrypt", e.toString());
			} catch (javax.crypto.IllegalBlockSizeException e) {
			    answer.xmlMessage(FactoryWriterXML.ERROR, "server:error:encrypt", e.toString());
			} catch (java.io.IOException e) {
			    answer.xmlMessage(FactoryWriterXML.ERROR, "server:error:encrypt", e.toString());
			} catch (Exception e) {
			    e.printStackTrace();
				localServer.returnException(answer, e);
			}
			shutDownClient();
		}
    }
	
    /** Executes a query and keep the results in the queue. */
    private void queryQueue(FactoryWriterXML query) {
        query(query, errQueue);
    }
    
    /** Connects the client to the server. */
    public void connect(FrameMain frame, 
						String serverName) {
		if(isConnected()) return;
		
		Server server = localServer.getServer(serverName);
        if(server == null) {
            frame.addMessageDictionary("ERR", "error:unknowserver", serverName);
            return;            
        }

        this.replication = server.getReplication();
        String serverAddress = server.getAddress();
		String encryptKey = server.getEncryptKey();
        int port = server.getPort();

		ecipher = null;
		dcipher = null;
		if(encryptKey != null && encryptKey.length() > 0) {
			try {
				// Create the key
				KeySpec keySpec = new PBEKeySpec(encryptKey.toCharArray(), 
													SALT, 
													ITERATIONCOUNT);
													
				SecretKey key = SecretKeyFactory.getInstance(ENCRYPTION).
									generateSecret(keySpec);
									
				ecipher = Cipher.getInstance(key.getAlgorithm());
				dcipher = Cipher.getInstance(key.getAlgorithm());

				// Prepare the parameter to the ciphers
				AlgorithmParameterSpec paramSpec = 
						new PBEParameterSpec(SALT, ITERATIONCOUNT);

				// Create the ciphers
				ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
				dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			} catch (java.security.InvalidAlgorithmParameterException e) {
				frame.addMessageDictionary("ERR", "error:encrypt", e.toString());
				ecipher = null;
				dcipher = null;
			} catch (java.security.spec.InvalidKeySpecException e) {
				frame.addMessageDictionary("ERR", "error:encrypt", e.toString());
				ecipher = null;
				dcipher = null;
			} catch (javax.crypto.NoSuchPaddingException e) {
				frame.addMessageDictionary("ERR", "error:encrypt", e.toString());
				ecipher = null;
				dcipher = null;
			} catch (java.security.NoSuchAlgorithmException e) {
				frame.addMessageDictionary("ERR", "error:encrypt", e.toString());
				ecipher = null;
				dcipher = null;
			} catch (java.security.InvalidKeyException e) {
				frame.addMessageDictionary("ERR", "error:encrypt", e.toString());
				ecipher = null;
				dcipher = null;
			}
		}
		
		if(serverAddress == null || serverAddress.length() == 0) {
            frame.addMessageDictionary("ERR", "error:noaddress", serverName);
            return;            
        }

        try {
            socketClient = new Socket(serverAddress, port);
            frame.addMessageDictionary("MSG", "server:open:client:ok", 
												serverName);
			socketClient.setSoTimeout(TIMEOUT);
			socketClient.setKeepAlive(true);
            socketClient.setSendBufferSize(C2SBUFFERSIZE);
            socketClient.setReceiveBufferSize(S2CBUFFERSIZE);

			if(ecipher == null || dcipher == null)
				frame.addMessageDictionary("MSG", "warning:noencrypt");

            outputClient = new OutputStreamWriter(
								new FactoryZipOutput(
									socketClient.getOutputStream(), 
									C2SBUFFERSIZE), "UTF-8");
			
            inputClient = new BufferedReader(
								new InputStreamReader(
									new FactoryZipInput(
										socketClient.getInputStream()), 
										"UTF-8"),
										S2CBUFFERSIZE);
										
        } catch (UnknownHostException e) {
            frame.addMessageDictionary("ERR", "server:unknownhost", 
												serverName, 
												serverAddress,
												"" + port);
            socketClient = null;
        } catch (ConnectException e) {
            frame.addMessageDictionary("ERR", "server:connectexception", 
												serverName, 
												serverAddress,
												"" + port);
            socketClient = null;
        } catch (NoRouteToHostException e) {
            frame.addMessageDictionary("ERR", "server:connectexception", 
												serverName, 
												serverAddress,
												"" + port);
            socketClient = null;
        } catch (java.io.IOException e) {
            frame.addMessage(e);
            socketClient = null;
        }
    }
    
    /** Disconnects the client from the server. */
    public void disconnect(FrameMain frame) {
		if(!isConnected()) return;
        frame.addMessageDictionary("MSG", "server:close:client");
        shutDownClient();
    }
    
    /**Indicates is the client is connected to one server. */
    public boolean isConnected() {
        return socketClient != null;
    }

    /** Attachs a client to the server. */
    public void attach(FrameMain frame) {
        synchronized(mainFrames) {
            mainFrames.add(frame);
        }
    }

    /** Detachs a client from the server. */
    public void detach(FrameMain frame) {
        synchronized(mainFrames) {
            mainFrames.remove(frame);
        }
    }

    /** Broadcasts a message to every client connected to the server. */
    boolean broadcastMessage(FactoryWriterXML answer) {
		boolean isError = false;
        synchronized(mainFrames) {
            for (FrameMain frame: mainFrames) {
                if(frame.addMessage(answer)) 
                    isError = true;
            }
        }
		return isError;
    }

    /** Broadcasts a message to every client connected to the server. */
    void broadcastMessage(String category, String label, 
						  String... args) {
        synchronized(mainFrames) {
            for (FrameMain frame: mainFrames) 
                frame.addMessageDictionary(category, label, args);
        }
        if(mainFrames.size() == 0 && !category.equals("TRC")) {
            System.out.println(XMLWrapper.dfLocal.format(new Date()) + 
                                ":[" + category + "] " + LocalMessage.get(label, args));
        }
    }
	
    /** Broadcasts a message to every client connected to the server. */
    private void broadcastMessage(Exception e) {
        synchronized(mainFrames) {
            for (FrameMain frame: mainFrames) 
                frame.addMessage(e);
        }
        if(mainFrames.size() == 0) {
            System.out.print(XMLWrapper.dfLocal.format(new Date()) + " [FAT] ");
            e.printStackTrace();
        }
    }

    /** Wait the initialization phase is finished. */
    private void waitInit() {
        if (threadInit != null) {
            try {
                while (!threadInit.isInitialized())
                    Thread.sleep(1000);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }

    /** Wait the initialization phase is finished for the main frame. */
    private void waitFrameInit() {
        try {
            Thread.sleep(5 *1000);
        } catch (InterruptedException ex) {
            return;
        }
        if(mainFrames.size() > 0) {
            try {
                while (!mainFrames.get(0).isInitialized())
                    Thread.sleep(1000);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }

    /** Returns the output queue. */
    public FactoryWriterXML getQueue() {
        waitInit();
        return outputQueue;
    }

    /** Returns the error queue. */
    public FactoryWriterXML getErrorQueue() {
        waitInit();
        return errQueue;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml) {
        for(FactorySession session: sessions) 
            session.xmlOut(xml);
    }

    /**
     * Thread used in order to save data in the background.
     */
    private class ThreadSave implements Runnable {

        /** Constructor. Initializes the thread. */
        private ThreadSave() {
            Thread runner = new Thread(this);
            runner.start();
        }

        /** Infinite loop: data is saved using a timer. */
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3 * 60 * 1000);
                } catch (InterruptedException ex) {
                    return;
                }
                for (FrameMain frame: mainFrames) {
                    try {
                        frame.saveSession();
                    } catch (Exception e) {
                        frame.addMessage(e);
                    }
                }
                if(mainFrames.size() == 0) {
                    FactoryWriterXML answer = new FactoryWriterXML();
                    FactoryWriterXML query = new FactoryWriterXML("query:saveall");
                    try {
                        queryLocal(query, answer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     *  Thread used during initialization phase.
     */
    private class ThreadInit implements Runnable {

        /** Indicates the thread is initialized. */
        private volatile boolean initialized;

        /** Indicates the thread is terminated. */
        private volatile boolean terminated = false;

		/** Encryption cipher. */
		private Cipher ecipher;

		/** Decryption cipher. */
		private Cipher dcipher;
		
        /** Constructor. Initializes the thread. */
        ThreadInit() {
            Thread runner = new Thread(this);
            runner.start();
        }

        /** Indicates the thread is initialized. */
        public boolean isInitialized() {
            return initialized;
        }

        /** Foreces the thread to terminate. */
        public void terminate() {
            terminated = true;
        }

        /** Initializes the sub-servers and open the server. */
        public void run() {
			if(localServer == null) {
				// Opens local database
				try {
					localServer = createServer();
					localSession = new FactorySession(FactoryConnection.this, 
													  localServer);
					errQueue = new FactoryWriterXML();
					FactoryWriterXML query = new FactoryWriterXML("query:open");
					queryQueue(query);
				} catch (Exception e) {
					broadcastMessage(e);
					return;
				}
				finally {
					initialized = true;
				}

                // Starts the save thread
                new ThreadSave();
                
			    // Starts the replication deamon
			    new ThreadReplicate();
			    
			    // Starts the email transport deamon
			    new ThreadMailTransport();
			    
			    Server serverDef = localServer.getServer("localhost"); 
			    if(serverDef == null || !serverDef.isActive()) 
			        return;

				waitInit();
				waitFrameInit();

				// Starts the server
				Selector selector;
				int port = localServer.getLocalPort();
				try {
					ServerSocketChannel sockChannel = ServerSocketChannel.open();
					sockChannel.configureBlocking(false);
					InetSocketAddress server = new InetSocketAddress(port);
					ServerSocket socket = sockChannel.socket();
					socket.setReuseAddress(true);
					socket.bind(server);
					selector = Selector.open();
					sockChannel.register(selector, SelectionKey.OP_ACCEPT);
					broadcastMessage("SRV", "server:running", "" + port);
				} catch (SocketException e) {
					broadcastMessage("SRV", "server:portuse", "" + port);
					return;
				} catch (Exception e) {
					broadcastMessage(e);
					return;
				}
				
				String encryptKey = localServer.getLocalEncryptKey();

				ecipher = null;
				dcipher = null;
				if(encryptKey != null && encryptKey.length() > 0) {
					try {
						// Create the key
						KeySpec keySpec = new PBEKeySpec(encryptKey.toCharArray(), 
															SALT, 
															ITERATIONCOUNT);
															
						SecretKey key = SecretKeyFactory.getInstance(ENCRYPTION).
											generateSecret(keySpec);
											
						ecipher = Cipher.getInstance(key.getAlgorithm());
						dcipher = Cipher.getInstance(key.getAlgorithm());

						// Prepare the parameter to the ciphers
						AlgorithmParameterSpec paramSpec = 
								new PBEParameterSpec(SALT, ITERATIONCOUNT);

						// Create the ciphers
						ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
						dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
					} catch (java.security.InvalidAlgorithmParameterException e) {
						broadcastMessage("ERR", "error:encrypt", e.toString());
						ecipher = null;
						dcipher = null;
					} catch (java.security.spec.InvalidKeySpecException e) {
						broadcastMessage("ERR", "error:encrypt", e.toString());
						ecipher = null;
						dcipher = null;
					} catch (javax.crypto.NoSuchPaddingException e) {
						broadcastMessage("ERR", "error:encrypt", e.toString());
						ecipher = null;
						dcipher = null;
					} catch (java.security.NoSuchAlgorithmException e) {
						broadcastMessage("ERR", "error:encrypt", e.toString());
						ecipher = null;
						dcipher = null;
					} catch (java.security.InvalidKeyException e) {
						broadcastMessage("ERR", "error:encrypt", e.toString());
						ecipher = null;
						dcipher = null;
					}
				}

				if(ecipher == null || dcipher == null)
					broadcastMessage("SRV", "warning:noencrypt");

				// Enters an infinite loop in order to listen
				while (!terminated) {
					try {
						selector.select();
						Set keys = selector.selectedKeys();
						Iterator it = keys.iterator();
						while (it.hasNext()) {
							SelectionKey selKey = (SelectionKey)it.next();
							it.remove();
							if (selKey.isAcceptable()) {
								ServerSocketChannel selChannel = 
									(ServerSocketChannel) selKey.channel();
								ServerSocket selSocket = selChannel.socket();
								// Start conversation with the client
								new ThreadListener(selSocket.accept(),
													ecipher, 
													dcipher);
							}
						}
					} catch (Exception e) {
						broadcastMessage(e);
					}
				}
			}
			else 				
				initialized = true;
        }
    }

    /**
     *  Thread used as listener.
     */
    private class ThreadListener implements Runnable {

        /** Socket managed by the server. */
        private Socket socketServer;

		/** Buffer for server output. */
		private OutputStreamWriter outputServer;
		
		/** Buffer for server input. */
		private BufferedReader inputServer;

		/** Encryption cipher. */
		private Cipher ecipher;

		/** Decryption cipher. */
		private Cipher dcipher;
		
        /** Constructor. Initializes the thread. */
        ThreadListener(Socket socketServer, Cipher ecipher, Cipher dcipher) {
            this.socketServer = socketServer;
			this.ecipher = ecipher;
			this.dcipher = dcipher;
			try {
				socketServer.setKeepAlive(true);
			    socketServer.setSendBufferSize(S2CBUFFERSIZE);
			    socketServer.setReceiveBufferSize(C2SBUFFERSIZE);
			}
			catch(SocketException ex) {
				broadcastMessage(ex);
			}
            Thread runner = new Thread(this);
            runner.start();
        }
		
        /** Initializes the sub-servers and opens the server. */
        public void run() {
			FactorySession session = 
				new FactorySession(FactoryConnection.this, 
									localServer, 
									true);
			synchronized(sessions) {
				sessions.add(session);
			}

			try {
				String address =
					socketServer.getInetAddress().getCanonicalHostName();
					
				broadcastMessage("SRV", "server:open:server", address);
				session.setHost(address);

				outputServer = new OutputStreamWriter(
									new FactoryZipOutput(
										socketServer.getOutputStream(), 
										S2CBUFFERSIZE), "UTF-8");

				inputServer = new BufferedReader(
								new InputStreamReader(
									new FactoryZipInput(
										socketServer.getInputStream()), 
									"UTF-8"),
									C2SBUFFERSIZE);

				String line;
				String buffer = "";
				while ((line = inputServer.readLine()) != null) {
					if(localServer == null) 
						break;
					if(line.startsWith("<end:request/>")) {
						String request = decrypt(dcipher, buffer);

						broadcastMessage("TRC", "server:request", 
							session.getOperatorName(),
							session.getHost(),
							request);

						FactoryWriterXML query = new FactoryWriterXML();
						FactoryWriterXML answer = new FactoryWriterXML();
						
						query.copyFrom(request);

						// Execute the request on local server
						localServer.query(session, query, answer);

						// Sends answer and messages
						String messagesString = answer.getErrWriter().toString();
						String answerString = answer.getOutWriter().toString();
						if(messagesString.length() > 0) {
							broadcastMessage("TRC", "server:answer", 
								session.getOperatorName(),
								session.getHost(),
								messagesString);

							outputServer.write(encrypt(ecipher, messagesString) + 
														"\r\n");
							outputServer.write("<end:messages/>\r\n");
						}
						if(answerString.length() > 0) {
							broadcastMessage("TRC", "server:answer", 
								session.getOperatorName(),
								session.getHost(),
								answerString);

							outputServer.write(encrypt(ecipher, answerString) + 
														"\r\n");
						}
						outputServer.write("<end:answer/>\r\n");
						outputServer.flush();

						if(request.startsWith("<query:disconnect>"))
							break;
						if(!session.isAuthorized()) 
							break;

						buffer = "";
					}
					else buffer += line;
				}
				broadcastMessage("SRV", "server:close:server", 
					session.getOperatorName(),
					session.getHost());
			} catch (IOException e) {
				broadcastMessage("SRV", "server:close:server", 
                    session.getOperatorName(),
					session.getHost());
			} catch (javax.crypto.BadPaddingException e) {
				broadcastMessage("SRV", "error:encrypt", e.toString());
				broadcastMessage("SRV", "server:close:server", 
                    session.getOperatorName(),
					session.getHost());
			} catch (javax.crypto.IllegalBlockSizeException e) {
				broadcastMessage("SRV", "error:encrypt", e.toString());
				broadcastMessage("SRV", "server:close:server", 
                    session.getOperatorName(),
					session.getHost());
			} catch (Exception e) {
				broadcastMessage(e);
				broadcastMessage("SRV", "server:close:server", 
                    session.getOperatorName(),
					session.getHost());
			}
			finally {
				synchronized(sessions) {
					sessions.remove(session);
				}
				try {
					outputServer.close();
					inputServer.close();
					socketServer.close();
				} catch(Exception e3) {
					return;
				}
			}
		}
    }

    /**
     *  Thread used for replication.
     */
    private class ThreadReplicate implements Runnable {

		/** List of classes used for replication. */
		private List<String> classes = new ArrayList(10);

        /** Constructor. Initializes the thread. */
        ThreadReplicate() {
			localServer.addClassList(classes);
            Thread runner = new Thread(this);
            runner.start();
        }
        
        /** Runs the replication. */
        public void run() {
			FactoryWriterXML query;
			FactoryWriterXML answer;
			while (true) {
				try {
					Thread.sleep(20 * 1000);
				} catch (InterruptedException ex) {
					return;
				}

				if(replication && isConnected()){
					try {
						broadcastMessage("TRC", "message:replication");

                        // Ask remote server for identification of network recipients
					    query = new FactoryWriterXML("query:getnetworkrecipients");
					    answer = new FactoryWriterXML();
                        queryRemote(query, answer);
					    broadcastMessage(answer);
                        
                        // Ask local server to update actors based on network recipients
                        query = new FactoryWriterXML();
					    String details = answer.getOutWriter().toString();
                        details = XMLWrapper.replaceAll(details, 
                                 "<response", "<query:replicate");
                        details = XMLWrapper.replaceAll(details, 
                                 "</response", "</query:replicate");
                        query.copyFrom(details);
                        answer = new FactoryWriterXML();
                        queryLocal(query, answer);
                        broadcastMessage(answer);

						for(int i = 0 ; i<classes.size() && 
											replication && 
											isConnected(); i++) {
							String className = classes.get(i);
						    broadcastMessage("TRC", "message:replication:class", 
                                                    className);
                            
						    query = new FactoryWriterXML("query:list");
						    query.xmlOut("class", className);
						    answer = new FactoryWriterXML();
                            
						    ImportListXML importListLocal = 
						        new ImportListXML(null, null);
						    queryLocal(query, answer);

                            importListLocal.xmlIn(answer, null, false);
						    broadcastMessage(answer);
                                
						    answer = new FactoryWriterXML();
						    ImportListXML importListRemote = 
						        new ImportListXML(null, null);
						    queryRemote(query, answer);

                            importListRemote.xmlIn(answer, null, false);
						    broadcastMessage(answer);
                                
						    importListLocal.cleanDuplicates(importListRemote);
						    importListRemote.cleanDuplicates(importListLocal);

							// phase 1: remote==>local
							// phase 2: local==>remote
							for(int phase=1 ; phase<=2 ; phase++) {

								ImportListXML importList = phase == 1 ? 
												importListRemote:
												importListLocal;

								if (importList == null || 
										!replication || 
										!isConnected()) 
										break;
										
								for (int k = 0; k < importList.getNamesCount() ; k++) {
								    String iidName = importList.getIid(k);
									String objectName = importList.getName(k);
									String objectClassName = importList.getClassName(k);
                                    
									if(objectName.length() == 0 ||
										objectName.startsWith("DEMO-"))
										continue;
										
									if(!replication || !isConnected())
										break;
									
								    query = new FactoryWriterXML("query:save");
								    query.xmlStart(objectClassName);
								    query.xmlOut("iid", iidName);
								    query.xmlOut("name", objectName);
								    query.xmlEnd();
								    answer = new FactoryWriterXML();
									if(phase == 1)
                                        queryRemote(query, answer);
                                    else
                                        queryLocal(query, answer);
									broadcastMessage(answer);

									if(!replication || !isConnected())
										break;
									
									details = answer.getOutWriter().toString();
									if(details.length()>0){
									    query = new FactoryWriterXML();
                                        details = XMLWrapper.replaceAll(details, 
                                                "<response", "<query:replicate");
									    details = XMLWrapper.replaceAll(details, 
                                                "</response", "</query:replicate");
                                        query.copyFrom(details);
                                        
									    answer = new FactoryWriterXML();
									    if(phase == 1)
									        queryLocal(query, answer);
									    else
									        queryRemote(query, answer);
										broadcastMessage(answer);
									}
								}
							}

						    try {
						        Thread.sleep(1000);
						    } catch (InterruptedException ex) {
						        return;
						    }
						}
					    broadcastMessage("TRC", "message:replication:end");
					} catch (Exception e) {
						broadcastMessage(e);
					}
                }
            }
        }
    }

    /**
     *  Thread used for email transport.
     */
    private class ThreadMailTransport implements Runnable {
	
		/** Socket used with mail servers. */
		private Socket socketMail;
		
		/** Output stream. */
		private OutputStreamWriter outputMail;
		
		/** Input stream. */
		private BufferedReader inputMail;
		
		/** Error count. */
		private int errors;
		
		/** List of recipients within actions sent by the system. */
		private List<Recipient> actionQueue = new ArrayList();

        /** Constructor. Initializes the thread. */
        ThreadMailTransport() {
            Thread runner = new Thread(this);
            runner.start();
        }
		
		/** Writes output. */
		private void out(String text) throws IOException {
			outputMail.write(text + "\r\n");
		}
		
		/** Creates an email based on action. */
		private boolean smtp(Actor sender, Action action) throws IOException {
			synchronized (action) {
				String from = "";
				String fromAddress = "";
				String to = "";

				for(EMail email:sender.emails) {
					if(email.principal) {
						from = "\"[Factory] " + 
								sender.getName() + 
								"\" <" +
								email.address + 
								">";
						fromAddress = "<" +
								email.address + 
								">";
						break;
					}
				}

				if(from.trim().length()==0)
					return false;

				for (Recipient recipient: action.recipients) {
					if(recipient.actor != null &&
							(recipient.status == Recipient.NOT_SENT ||
							recipient.status == Recipient.ERROR)) {
						for(EMail email:recipient.actor.emails) {
							if(to.length()>0) to += ",";
							to += "\"" + 
									recipient.actor.getName() + 
									"\" <" +
									email.address + 
									">";
						}
					}
				}

				if(to.length() > 0) {
					List<Entity> prerequisites = new ArrayList(20);
                    FactoryWriterXML xml = new FactoryWriterXML("factory", true);
					TransactionXML transaction = new TransactionXML(localSession, xml);
                    transaction.setCode(TransactionXML.SAVE);
					action.addPrerequisites(transaction, prerequisites);
					action.xmlOutRecipients(xml, transaction);
					for(Entity entity: prerequisites) 
						entity.xmlOut(xml, transaction, true);
					action.xmlOut(xml, transaction, true);

					String actionString = XMLWrapper.replaceAll(
											xml.getOutWriter().toString(),
											"\n",
											"\r\n");

					out("MAIL FROM:" + fromAddress);
					for (Recipient recipient: action.recipients) {
						if(recipient.actor != null &&
								(recipient.status == Recipient.NOT_SENT ||
								recipient.status == Recipient.ERROR)) {
							actionQueue.add(recipient);
							for(EMail email:recipient.actor.emails) {
								String outMail = "\"" + 
										recipient.actor.getName() + 
										"\" <" +
										email.address + 
										">";
								out("RCPT TO:" + outMail);
								broadcastMessage("MSG", 
										"message:sending", action.getName(), 
															outMail);
							}
						}
					}
					out("DATA");
					out("Mime-Version: 1.0");
					out("Content-Transfer-Encoding: 8bit");
					out("Content-Type: text/plain; charset=UTF-8; format=flowed");
					out("From: " + from.trim());
					out("To: " + to.trim());
					out("Subject:" + action.getName());
					out("Date: " + XMLWrapper.dfUS.format(new Date()));
					out("X-Mailer: " + localServer.getBuild());
					out("Message-Id: <" + action.getIid() + ">");
					out("");
					out(LocalMessage.get("instruction:mail"));
					out(localServer.getBuild());
					out(localServer.getCopyright());
					out(localServer.getHomePage());
					out("");
					out(actionString);
					out(".");
					outputMail.flush();
					return true;
				}
			}
			return false;
		}

		/** Creates emails based on pending actions. */
		private int smtp(Actor sender) throws IOException {
			int count = 0;
			for(Action action: localServer.actions.actions) {
				if(action.isActive() && 
					action.isFromActor(sender) && 
					smtp(sender, action))
					++count;
			}
			return count;
		}

		/** Clears the list of recipient within sent actions. */
		private void clearQueue() {
			for(Recipient recipient: actionQueue) {
				recipient.status = Recipient.SENT;
				localServer.actions.setDirty();
			}
			actionQueue.clear();
		}
		
		/** Sends emails. */
		private void smtp(Actor sender, Server server) {
			if(!server.activeOutMail ||
                server.outMailServer == null ||
				server.outMailServer.length() == 0 ||
				server.outMailPort == 0 ||
				server.outMailUserName == null ||
				server.outMailUserName.length() == 0 ||
				server.outMailPassword == null ||
				server.outMailPassword.length() == 0) 
					return;

			String base64UserName = 
					Base64.encodeBytes(
						server.outMailUserName.getBytes());
						
			String base64Password = server.outMailPassword;

			broadcastMessage("TRC", "message:mail:send", "");

			try {
				socketMail = new Socket(server.outMailServer, 
										server.outMailPort);
										
				socketMail.setSoTimeout(TIMEOUT);
				
				outputMail = new OutputStreamWriter(
								socketMail.getOutputStream()); 
				
				inputMail = new BufferedReader(
								new InputStreamReader(
									socketMail.getInputStream())); 
									
				outputMail.write("EHLO " + InetAddress.getLocalHost() + "\r\n");
				outputMail.flush();

				boolean error = false;
				String line;
				while ((line = inputMail.readLine()) != null) {
					broadcastMessage("TRC", "message:get", 
						XMLWrapper.wrapHTML(line));
					if(!line.startsWith("1") &&
							!line.startsWith("2") &&
							!line.startsWith("3")) {
						broadcastMessage("ERR", "error:mail:out", line);
						error = true;
						break;
					}
					else if (line.startsWith("250-AUTH")){
						String[] split = line.split(" ");
						if (split[1].equalsIgnoreCase("LOGIN")){
							outputMail.write("AUTH LOGIN\r\n");
							outputMail.write(base64UserName + "\r\n");
							outputMail.write(base64Password + "\r\n");
							break;
						}
					}
				}

				if(error)
					return;

				// Sends the emails
				int sent = smtp(sender);
				if(sent == 0) 
					return;
					
				error = false;
				int oks = 0;
				while ((line = inputMail.readLine()) != null) {
					broadcastMessage("TRC", "message:get", 
						XMLWrapper.wrapHTML(line));
					if(!line.startsWith("1") &&
							!line.startsWith("2") &&
							!line.startsWith("3")) {
						broadcastMessage("ERR", "error:mail:out", line);
						error = true;
						break;
					}
					String[] split = line.split(" ");
					if (split[0].equalsIgnoreCase("250") && 
						split[1].equalsIgnoreCase("OK")){
						++oks;
						if(oks == 3*sent)
							break;
					}
				}
				
				outputMail.write("QUIT\r\n");
				outputMail.flush();
				socketMail.close();

				if(error)
					return;
					
				// Update the actions
				clearQueue();
					
			} catch (UnknownHostException e) {
				++errors;
				broadcastMessage("ERR", 
									"server:unknownhost", 
									"localhost",
									server.outMailServer,
									"" + server.outMailPort);
			} catch (ConnectException e) {
				++errors;
				broadcastMessage("ERR", 
									"server:connectexception", 
									"localhost",
									server.outMailServer,
									"" + server.outMailPort);
			} catch (SocketTimeoutException e) {
				++errors;
				broadcastMessage("ERR", 
									"server:timeout:server", 
									"localhost",
									server.outMailServer,
									"" + server.outMailPort);
			} catch (NoRouteToHostException e) {
				++errors;
				broadcastMessage("ERR", 
									"server:connectexception", 
									"localhost",
									server.outMailServer,
									"" + server.outMailPort);
			} catch (java.io.IOException e) {
				++errors;
				broadcastMessage(e);
			}
		}
		
		/** Receives emails. */
		private void pop(Server server) {
			if(!server.activeInMail ||
                server.inMailServer == null ||
				server.inMailServer.length() == 0 ||
				server.inMailPort == 0 ||
				server.inMailUserName == null ||
				server.inMailUserName.length() == 0 ||
				server.inMailPassword == null ||
				server.inMailPassword.length() == 0) 
					return;

			String base64Password = 
					new String(Base64.decode(
						server.outMailPassword));

			broadcastMessage("TRC", "message:mail:receive", "");

			try {
				socketMail = new Socket(server.inMailServer, server.inMailPort);
				socketMail.setSoTimeout(TIMEOUT);
				
				outputMail = new OutputStreamWriter(
							socketMail.getOutputStream()); 
				
				inputMail = new BufferedReader(
							new InputStreamReader(
								socketMail.
									getInputStream())); 
									
				outputMail.write("USER " + server.inMailUserName +"\r\n");
				outputMail.write("PASS " + base64Password + "\r\n");
				outputMail.write("LIST\r\n");
				outputMail.flush();

				List<Integer> messages = new ArrayList(300);
				boolean error = false;
				String line;
				while ((line = inputMail.readLine()) != null) {
					broadcastMessage("TRC", "message:get", 
										XMLWrapper.wrapHTML(line));
					if(line.startsWith(".")) {
						break;
					} else if(line.startsWith("-")) {
						broadcastMessage("ERR", "error:mail:in", 
											XMLWrapper.wrapHTML(line));
						error = true;
						break;
					} else if(line.startsWith("+")) {
						continue;
					} else {
						String[] split = line.split(" ");
						if(split[0].length()>0) {
							int number;
							try {
								number = Integer.parseInt(split[0]);
								if(number > 0) 
									messages.add(new Integer(number));
							} catch (java.lang.NumberFormatException e) {
								number = 0;
							}
						}
					}
				}

				for(int i=messages.size() - 1 ; i >= 0 ; i--) {
					int number = messages.get(i).intValue();
					broadcastMessage("TRC", "message:get", "" + number);
					outputMail.write("RETR " + number + "\r\n");
					outputMail.flush();
					StringWriter queryAction = null;
					while ((line = inputMail.readLine()) != null) {
						broadcastMessage("TRC", "message:get", 
											XMLWrapper.wrapHTML(line));
						if(line.startsWith(".")) {
							break;
						} else if(line.indexOf("</factory>") >= 0) {
							if(queryAction != null) 
								queryAction.write(line);
							break;
						} else if(line.indexOf("<factory>") >= 0) {
							queryAction = new StringWriter();
							queryAction.write(line);
						} else if(queryAction != null) {
							queryAction.write(line + "\n");
						}
					}
					
					if(queryAction != null) {
						broadcastMessage("TRC", "message:get", 
											XMLWrapper.wrapHTML(queryAction.toString()));

						FactoryWriterXML query = new FactoryWriterXML();
						String actionString = queryAction.toString();
						actionString = XMLWrapper.replaceAll(actionString, 
                                                "<factory", "<query:replicate");
						actionString = XMLWrapper.replaceAll(actionString, 
                                                "</factory", "</query:replicate");
						query.copyFrom(actionString);
						FactoryWriterXML answer = new FactoryWriterXML();
						queryLocal(query, answer);
						String answerString = answer.getErrWriter().toString();
						if(answerString.indexOf("message:replicated:created:") < 0 &&
						   answerString.indexOf("message:replicated:updated:") < 0) {
						   if(!localServer.isAnyServerDirty()) {
								//eMail can be removed from mailbox
								outputMail.write("DELE " + number + "\r\n");
								outputMail.flush();
							}
						}
						broadcastMessage(answer);
					}
				}

				outputMail.write("QUIT\r\n");
				outputMail.flush();
				socketMail.close();
				if(error)
					return;
			} catch (UnknownHostException e) {
				++errors;
				broadcastMessage("ERR", 
									"server:unknownhost", 
									"localhost",
									server.inMailServer,
									"" + server.inMailPort);
			} catch (SocketTimeoutException e) {
				++errors;
				broadcastMessage("ERR", 
									"server:timeout:server", 
									"localhost",
									server.inMailServer,
									"" + server.inMailPort);
			} catch (ConnectException e) {
				++errors;
				broadcastMessage("ERR", 
									"server:connectexception", 
									"localhost",
									server.inMailServer,
									"" + server.inMailPort);
			} catch (NoRouteToHostException e) {
				++errors;
				broadcastMessage("ERR", 
									"server:connectexception", 
									"localhost",
									server.inMailServer,
									"" + server.inMailPort);
			} catch (java.io.IOException e) {
				++errors;
				broadcastMessage(e);
			}
		}
		
        /** Runs the transport. */
        public void run() {
			// when the number of errors is greater than 6,
			// then the system halts.
			while (errors < 6) {
				try {
					Server server = localServer.getServer("localhost"); 
					Actor sender = localSession.getOperator();
						
					try {
						Thread.sleep(30 * 1000);
					} catch (InterruptedException ex) {
						return;
					}

                    if(server != null && sender != null) {
                        pop(server);
                        smtp(sender, server);
                    }
					errors = 0;
				} catch (Exception e) {
					++errors;
					broadcastMessage(e);
                }
            }
        }
    }
}
