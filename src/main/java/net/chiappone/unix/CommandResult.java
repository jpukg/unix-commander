package net.chiappone.unix;

/**
 * Represents the result of a Unix command (e.g. output text and exit code
 * value).
 *
 * @author Kurtis Chiappone
 */
public class CommandResult {

    private int exitValue = 0;
    private String output = null;

    public CommandResult() {

    }

    /**
     * @param exitValue
     * @param output
     */
    public CommandResult( int exitValue, String output ) {

        this.exitValue = exitValue;
        this.output = output;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override public boolean equals( Object obj ) {

        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        CommandResult other = (CommandResult) obj;
        if ( exitValue != other.exitValue )
            return false;
        if ( output == null ) {
            if ( other.output != null )
                return false;
        } else if ( !output.equals( other.output ) )
            return false;
        return true;
    }

    /**
     * @return the exitValue
     */
    public int getExitValue() {

        return exitValue;
    }

    /**
     * @param exitValue the exitValue to set
     */
    public void setExitValue( int exitValue ) {

        this.exitValue = exitValue;
    }

    /**
     * @return the output
     */
    public String getOutput() {

        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput( String output ) {

        this.output = output;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + exitValue;
        result = prime * result + ( ( this.output == null ) ? 0 : this.output.hashCode() );
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override public String toString() {

        return output;
    }

}
