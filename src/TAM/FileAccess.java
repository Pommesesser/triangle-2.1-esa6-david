/*
 * Copyright (C) 2016 J. Oppermann and A. Koch
 * Embedded Systems and Applications Group
 * Department of Computer Science, Technische Universitaet Darmstadt,
 * Hochschulstr. 10, 64289 Darmstadt, Germany.
 * 
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package TAM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Stefan Kropp
 *
 */
public class FileAccess {

  private FileReader fr;
  private BufferedReader br;
  private FileWriter fw;
  private BufferedWriter bw;

  private String filename;
  private int filehandle;

  /**
   * @param fr
   * @param br
   */
  public FileAccess(final FileReader fr, final BufferedReader br, final String filename, final int filehandle) {
    this.fr = fr;
    this.br = br;
    this.filename = filename;
    this.filehandle = filehandle;
    fw = null;
    bw = null;
  }

  /**
   * @param fw
   * @param bw
   */
  public FileAccess(final FileWriter fw, final BufferedWriter bw, final String filename, final int filehandle) {
    this.fw = fw;
    this.bw = bw;
    this.filename = filename;
    this.filehandle = filehandle;
    fr = null;
    br = null;
  }

  public void closeAll() throws IOException {
    if (br != null && fr != null) {
      br.close();
      fr.close();
    }
    if (bw != null && fw != null) {
      bw.close();
      fw.close();
    }
  }

  public boolean forReading() {
    return fr != null;
  }

  public boolean forWriting() {
    return fw != null;
  }

  /**
   * @return Returns the filename.
   */
  public String getFilename() {
    return filename;
  }

  /**
   * @return Returns the br.
   */
  public BufferedReader getBr() {
    return br;
  }

  /**
   * @return Returns the bw.
   */
  public BufferedWriter getBw() {
    return bw;
  }

  /**
   * @return Returns the fr.
   */
  public FileReader getFr() {
    return fr;
  }

  /**
   * @return Returns the fw.
   */
  public FileWriter getFw() {
    return fw;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object arg0) {
    return filehandle == ((FileAccess) arg0).filehandle;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return filehandle;
  }

}
