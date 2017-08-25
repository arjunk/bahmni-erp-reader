package org.bahmni.erp.reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


public class Application {


    public static void main(String[] args) throws MalformedURLException {
        System.out.println("I am Main");
        Application  app = new Application();
        Vector<String> databaseList = app.getDatabaseList("local", 8069);
        System.out.println(databaseList);
        int connect = app.connect("local", 8069, "openerp", "admin", "password");
        System.out.println(connect);

    }

    public Vector<String> getDatabaseList(String host, int port) throws MalformedURLException {
        XmlRpcClient xmlrpcDb = new XmlRpcClient();

        XmlRpcClientConfigImpl xmlrpcConfigDb = new XmlRpcClientConfigImpl();
        xmlrpcConfigDb.setEnabledForExtensions(true);
        xmlrpcConfigDb.setServerURL(new URL("http", host, port, "/xmlrpc/db"));

        xmlrpcDb.setConfig(xmlrpcConfigDb);
        Vector<String> res = new Vector<String>();
        try {
            //Retrieve databases
            Vector<Object> params = new Vector<Object>();
            Object result = xmlrpcDb.execute("list", params);
            Object[] a = (Object[]) result;

            System.out.println(a.length);
            System.out.println(a.getClass());
            for (int i = 0; i < a.length; i++) {
                if (a[i] instanceof String) {
                    res.addElement((String) a[i]);
                }
            }

        }
        catch(XmlRpcException e){
            System.out.println("XmlException Error while retrieving OpenERP Databases: ");
            }
        catch(Exception e)
            {
                System.out.println("Error while retrieving OpenERP Databases: ");
            }
        return res;
    }

    public int connect(String host, int port, String tinydb, String login, String password) throws MalformedURLException {
        XmlRpcClient xmlrpcLogin = new XmlRpcClient();

        XmlRpcClientConfigImpl xmlrpcConfigLogin = new XmlRpcClientConfigImpl();
        xmlrpcConfigLogin.setEnabledForExtensions(true);
        xmlrpcConfigLogin.setServerURL(new URL("http",host,port,"/xmlrpc/common"));

        xmlrpcLogin.setConfig(xmlrpcConfigLogin);

        try {
            //Connect
            Object[] params = new Object[] {tinydb,login,password};
            Object id = xmlrpcLogin.execute("login", params);
            if (id instanceof Integer)
                return (Integer)id;
            return -1;
        }
        catch (XmlRpcException e) {
            System.out.println("XmlException Error while logging to OpenERP: ");
            return -2;
        }
        catch (Exception e)
        {
            System.out.println("Error while logging to OpenERP: ");
            return -3;
        }
    }

    public void search(String host, int port) throws MalformedURLException {
        XmlRpcClient xmlrpcLogin = new XmlRpcClient();

        XmlRpcClientConfigImpl xmlrpcConfigLogin = new XmlRpcClientConfigImpl();
        xmlrpcConfigLogin.setEnabledForExtensions(true);
        xmlrpcConfigLogin.setServerURL(new URL("http",host,port,"/xmlrpc/object"));

        xmlrpcLogin.setConfig(xmlrpcConfigLogin);

        Object[] params = new Object[] {"openerp","admin","password"};
        try {
            Object id = xmlrpcLogin.execute("search", params);

        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
    }

    private Object [] read(XmlRpcClient xmlRpcLogin, String dbName, int uid, String password, String table, ArrayList<Object> ids, ArrayList<Object>fields) throws XmlRpcException{
        Vector<Object> readFunct = new Vector<Object>();
        readFunct.add(dbName); //Database name
        readFunct.add(uid);//uid of login user
        readFunct.add(password);//password of login user
        readFunct.add(table);//relation i.e. table to search
        readFunct.add("read");
        readFunct.add(ids);
        readFunct.add(fields);
        Object [] ob = (Object [])xmlRpcLogin.execute("execute",readFunct);
        return ob;
    }//read method
}
