package org.bahmni.erp.reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

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
        app.search("local",8069, connect);

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

    public void search(String host, int port, Integer uid) throws MalformedURLException {
        XmlRpcClient xmlrpcClient = getXmlRpcClient(host, port);

//        Object[] names = new Object[] {"state", "=", "draft"};
        Object[] params = new Object[] {"openerp",uid,"password", "purchase.order", "search"};

        Object r[]=new Object[6];
        r[0]="openerp";
        r[1]=(Integer)uid;
        r[2]="password";
        r[3]="purchase.order";
        r[4]="search";

        Object names[]=new Object[3];



        names[0]="state";
        names[1]="=";
        names[2] = "draft";

        Vector param2 = new Vector();
        param2.addElement(names);
        r[5]=param2;
        Object po_ids;
        try {
            po_ids=xmlrpcClient.execute("execute", r);
            Object[] d = (Object[]) po_ids;

            Integer i=0;

            for(i=0; i<d.length; i++){
                System.out.println((Integer)d[i]);
            }


        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
    }

    private XmlRpcClient getXmlRpcClient(String host, int port) throws MalformedURLException {
        XmlRpcClient xmlrpcClient = new XmlRpcClient();

        XmlRpcClientConfigImpl xmlrpcConfig = new XmlRpcClientConfigImpl();
        xmlrpcConfig.setEnabledForExtensions(true);
        xmlrpcConfig.setServerURL(new URL("http",host,port,"/xmlrpc/object"));

        xmlrpcClient.setConfig(xmlrpcConfig);
        return xmlrpcClient;
    }
}
