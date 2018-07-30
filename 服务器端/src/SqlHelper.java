import javax.xml.transform.Result;
import java.sql.*;

/**
 * Created by L on 2016/10/8.
 */
public class SqlHelper {
    String userId="ym";
    String password="123456";
    String driverName = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/L_project_db?characterEncoding=utf-8";//对这个数据库来说就是本地的
    PreparedStatement ps=null;
    Connection ct=null;
    ResultSet rs=null;//结果集

    public boolean ConnectDB()
    {
        boolean isConnected=true;
        try
        {
            Class.forName(driverName);
            ct= DriverManager.getConnection(url,userId,password);
        }catch (Exception e)
        {
            isConnected=false;
            e.printStackTrace();
        }
        return  isConnected;
    }

    //创建新的表或数据库
    public boolean create(String sql)
    {
        boolean isOK=true;
        try
        {
            ConnectDB();
            ps=ct.prepareStatement(sql);
            isOK=!ps.execute();//返回false代表成功
        }catch (Exception e)
        {
            e.printStackTrace();
            isOK=false;
        }
        return isOK;
    }

    //查询
    public ResultSet query(String sql,String[]paras) {
        try {
            ConnectDB();
            ps = ct.prepareStatement(sql);
            for(int i=0;i<paras.length;i++)
            {
                ps.setString(i+1,paras[i]);
            }
            rs=ps.executeQuery();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return rs;
    }

    //增删改
    public boolean updExecute(String sql,String[]paras)
    {
        boolean b=true;
        try
        {
            ConnectDB();
            ps=ct.prepareStatement(sql);
            for (int i = 0; i < paras.length; i++) {
                ps.setString(i + 1, paras[i]);
            }
            ps.executeUpdate();// 执行操作 返回变化的行数
        }catch (Exception e)
        {
            e.printStackTrace();
            b=false;
        }finally {
            this.close();
        }
        return b;
    }

    public void close()
    {
        try
        {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
            if (ct != null)
                ct.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
