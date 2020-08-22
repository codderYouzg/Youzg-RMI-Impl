package edu.youzg.rmi_impl.core;

import edu.youzg.util.XMLParser;
import edu.youzg.util.exceptions.XMLIsInexistentException;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * 扫描指定的配置文件，<br/>
 * 并将映射关系，存储到一个map中
 */
public class RMIFactory {
    private static final Map<String, RMIDefinition> rmiPool
            = new HashMap<>();

    public RMIFactory() {
    }

    /**
     * 扫描指定的配置文件，<br/>
     * 并将映射关系，存储到一个map中
     * @param xmlPath 目标xml文件的路径
     */
    public static void scanRMIMapping(String xmlPath) {
        try {
			new XMLParser() {

			    @Override
			    public void dealElement(Element element, int index) {
			        String interfaceStr = element.getAttribute("interface");
			        String classStr = element.getAttribute("class");

			        try {
			            Class<?> klass = Class.forName(classStr);
			            Object object = klass.newInstance();

			            RMIDefinition rmiDefinition = new RMIDefinition(klass, object);

			            rmiPool.put(interfaceStr, rmiDefinition);
			        } catch (ClassNotFoundException e) {
			            e.printStackTrace();
			        } catch (IllegalAccessException e) {
			            e.printStackTrace();
			        } catch (InstantiationException e) {
			            e.printStackTrace();
			        }
			    }
			}.parseTag(XMLParser.getDocument(xmlPath), "mapping");
		} catch (XMLIsInexistentException e) {
			e.printStackTrace();
		}

    }

    /**
     * 通过接口名，查询对应的RMIDefinition
     * @param interfaceName 目标接口名
     * @return 该接口 对应的 RMIDefinition
     */
    public static RMIDefinition getRmiDefinition(String interfaceName) {
        return rmiPool.get(interfaceName);
    }

}