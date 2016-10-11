package net.chiappone.unix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kurtis Chiappone
 */
public class UnixCommander {

    private static final Logger logger = LoggerFactory.getLogger( UnixCommander.class );
    private static final Runtime runtime = Runtime.getRuntime();

    /**
     * Destroys the process, closes the reader and stream.
     *
     * @param process the process to destroy
     * @param reader  the reader to close
     * @param stream  the stream to close
     */
    private void closeResources( Process process, BufferedReader reader, InputStream stream ) {

        try {

            if ( process != null ) {
                process.destroy();
            }

        } catch ( Exception e ) {
        }

        try {

            if ( stream != null ) {
                stream.close();
            }

        } catch ( Exception e ) {
        }

        try {

            if ( reader != null ) {
                reader.close();
            }

        } catch ( Exception e ) {
        }

    }

    /**
     * Execute the list of commands.
     *
     * @param commands list of commands to execute
     * @return CommandResult
     */
    public CommandResult execute( List<String> commands ) {

        return execute( commands, true );

    }

    /**
     * Execute the list of commands and choose whether or not to log the output.
     *
     * @param commands  list of commands to execute
     * @param logOutput true to log the output
     * @return CommandResult
     */
    public CommandResult execute( List<String> commands, boolean logOutput ) {

        return execute( commands, logOutput, true );

    }

    /**
     * Execute the list of commands, choose whether or not to log the output, and choose whether or not we should
     * wait for the process to complete.
     *
     * @param commands       list of commands to execute
     * @param logOutput      true to log the output
     * @param waitForProcess true to wait for the process to complete
     * @return CommandResult
     */
    public CommandResult execute( List<String> commands, boolean logOutput, boolean waitForProcess ) {

        ProcessBuilder pb = new ProcessBuilder( commands );
        pb.redirectErrorStream();
        Process process = null;

        try {

            process = pb.start();

        } catch ( IOException e ) {

            logger.error( "Error starting process", e );

        }

        return executeProcess( process, commands, logOutput, waitForProcess );

    }

    /**
     * Execute the given command.
     *
     * @param command the command to execute
     * @return CommandResult
     */
    public CommandResult execute( String command ) {

        return execute( command, true );

    }

    /**
     * Execute the given command choose whether or not to log the output.
     *
     * @param command   the command to execute
     * @param logOutput true to log the output
     * @return CommandResult
     */
    public CommandResult execute( String command, boolean logOutput ) {

        List<String> commands = new ArrayList<String>();
        commands.add( command );
        Process process = null;

        try {

            process = runtime.exec( command );

        } catch ( IOException e ) {

            logger.error( "Error executing process", e );

        }

        return executeProcess( process, commands, logOutput, true );

    }

    /**
     * Execute the list of commands on a remote host.
     *
     * @param user     the username to use for SSH
     * @param host     the remote host
     * @param commands the list of commands to execute
     * @return CommandResult
     */
    public CommandResult execute( String user, String host, List<String> commands ) {

        return execute( user, host, commands, true );

    }

    /**
     * Execute the list of commands on a remote host. Choose whether or not to log the output.
     *
     * @param user      the username to use for SSH
     * @param host      the remote host
     * @param commands  the list of commands to execute
     * @param logOutput true to log the output
     * @return CommandResult
     */
    public CommandResult execute( String user, String host, List<String> commands, boolean logOutput ) {

        return execute( user, host, commands, logOutput, true );

    }

    /**
     * Execute the list of commands on a remote host. Choose whether or not to log the output, and whether or not
     * we should wait for the procss to complete.
     *
     * @param user           the SSH user
     * @param host           the remote host
     * @param commands       the list of commands to execute
     * @param logOutput      true to log the output
     * @param waitForProcess true to wait for the process to complete
     * @return CommandResult
     */
    public CommandResult execute( String user, String host, List<String> commands, boolean logOutput,
                    boolean waitForProcess ) {

        List<String> newCommands = new ArrayList<String>();
        newCommands.add( "ssh" );
        newCommands.add( "-q" );
        newCommands.add( user + "@" + host );
        newCommands.add( "-o" );
        newCommands.add( "StrictHostKeyChecking=no " );
        newCommands.addAll( commands );
        return execute( newCommands, logOutput, waitForProcess );

    }

    /**
     * Execute the given command on a remote host.
     *
     * @param user    the SSH user
     * @param host    the remote host
     * @param command the command to execute
     * @return CommandResult
     */
    public CommandResult execute( String user, String host, String command ) {

        return execute( user, host, command, true );

    }

    /**
     * Execute the given command on a remote host. Choose whether or not to log the output.
     *
     * @param user      the SSH user
     * @param host      the remote host
     * @param command   the command to execute
     * @param logOutput true to log the output
     * @return CommandResult
     */
    public CommandResult execute( String user, String host, String command, boolean logOutput ) {

        command = "ssh -q " + user + "@" + host + " -o StrictHostKeyChecking=no " + command;
        return execute( command, logOutput );

    }

    /**
     * Execute the given command on a remote host. Choose whether or not to log the output, and whether or not we
     * should wait for the process to complete.
     *
     * @param process        the process running as a result of the commands
     * @param commands       the list of commands to execute
     * @param logOutput      true to log the output
     * @param waitForProcess true to wait for the process to complete
     * @return CommandResult
     */
    private CommandResult executeProcess( Process process, List<String> commands, boolean logOutput,
                    boolean waitForProcess ) {

        if ( process == null ) {

            logger.error( "Process is null!" );
            return new CommandResult( -1, "Null process" );

        }

        String result = "";
        int exitValue = 0;

        // If we're waiting for the process to complete, read all of the output

        if ( waitForProcess ) {

            BufferedReader reader = null;
            InputStream stream = null;

            try {

                stream = process.getInputStream();
                reader = new BufferedReader( new InputStreamReader( stream ) );

                String line = null;
                boolean multiLine = false;

                while ( ( line = reader.readLine() ) != null ) {

                    if ( multiLine ) {

                        result += "\n" + line;

                    } else {

                        result += line;
                        multiLine = true;

                    }

                }

                exitValue = process.waitFor();

            } catch ( IOException e ) {

                logger.debug( "Error handling stream", e );

            } catch ( Exception e ) {

                logger.error( "Error executing command", e );

            } finally {

                closeResources( process, reader, stream );

            }

        }

        // Otherwise, just read the first line and return

        else {

            BufferedReader reader = null;
            InputStream stream = null;

            try {

                stream = process.getInputStream();
                reader = new BufferedReader( new InputStreamReader( stream ) );

                String line = reader.readLine();

                if ( line != null ) {

                    result = line;

                }

            } catch ( IOException e ) {

                logger.debug( "Error handling stream", e );

            } catch ( Exception e ) {

                logger.error( "Error executing command", e );

            } finally {

                closeResources( process, reader, stream );

            }

        }

        StringBuilder buffer = new StringBuilder();

        for ( String s : commands ) {
            buffer.append( s + " " );
        }

        logCommand( logOutput, buffer.toString(), result, exitValue );
        return new CommandResult( exitValue, result );

    }

    /**
     * Logs the output of the given command.
     *
     * @param logOutput true to log the output
     * @param command   the command to log
     * @param result    the result of the command
     * @param exitValue the exit value of the command
     */
    private void logCommand( boolean logOutput, String command, String result, int exitValue ) {

        // Log the command and the output

        if ( logOutput ) {

            if ( exitValue == 0 ) {

                if ( logOutput && !result.isEmpty() ) {

                    logger.debug( command + "\n\t" + result );

                } else {

                    logger.debug( command );

                }

            } else {

                if ( logOutput && !result.isEmpty() ) {

                    logger.debug( command + "\n\t" + result + "\n\tExit value = " + exitValue );

                } else {

                    logger.debug( command + "\n\tExit value = " + exitValue );

                }

            }

        }

        // Just log the command and exit value

        else {

            if ( exitValue == 0 ) {

                logger.debug( command );

            } else {

                logger.debug( command + "\n\tExit value = " + exitValue );

            }

        }

    }

    /**
     * Executes SCP on a remote host.
     *
     * @param user the SSH user
     * @param host the remote host
     * @param from source file
     * @param to   destination file
     * @return CommandResult
     */
    public CommandResult scp( String user, String host, String from, String to ) {

        return execute( "scp -o StrictHostKeyChecking=no " + user + "@" + host + ":" + from + " " + to );

    }

}