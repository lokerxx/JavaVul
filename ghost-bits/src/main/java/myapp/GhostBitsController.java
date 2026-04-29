package myapp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping
public class GhostBitsController {

    private final Path labRoot = Paths.get("/tmp/ghost-bits-lab");
    private final Path uploadRoot = labRoot.resolve("uploads");
    private final Path publicRoot = labRoot.resolve("public");
    private final Path secretRoot = labRoot.resolve("secret");

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadRoot);
        Files.createDirectories(publicRoot);
        Files.createDirectories(secretRoot);
        writeIfMissing(publicRoot.resolve("hello.txt"), "hello from ghost-bits public area\n");
        writeIfMissing(secretRoot.resolve("flag.txt"), "flag{ghost_bits_path_escape_demo}\n");
    }

    @GetMapping(value = {"/", "/ghost-bits", "/playground"}, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String index() {
        String autoTypePayload = ghostJson("{\"@type\":\"java.awt.Rectangle\",\"x\":0,\"y\":0,\"width\":0,\"height\":0}");
        String usernamePayload = ghostJson("{\"username\":\"root@localhost\"}");
        String absoluteReadPayload = "阮严灵丰丰甲来/阮严灵丰丰甲来/阮严灵丰丰甲来/阮严灵丰丰甲来/etc/passw%64";
        String sqlPayload = ghostAscii("1 union select user, password from users");
        String xssPayload = ghostAscii("<script>alert(1)</script>");
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>Ghost Bits Playground</title>"
                + style()
                + "</head><body><main class=\"shell\"><section class=\"hero\"><p class=\"eyebrow\">Cast Attack / Ghost Bits</p><h1>Ghost Bits Playground</h1><p class=\"lead\">演示重点不是“中文危险”，而是“检查阶段看到的字符串”和“执行阶段落下去的低字节”不是同一个东西。这个页面现在按攻击链分组展示，便于先看 low-byte 变形，再看路径、协议、解析器和业务 sink 如何接住同一份输入。</p><div class=\"hero-actions\"><a href=\"#lab\">开始实验</a><a href=\"/api/status\">查看状态</a></div></section>"
                + "<section class=\"pipeline\"><div class=\"pipeline-title\"><h2>三段对照视图</h2><p>建议用同一套观察方式去点每个按钮：原始输入阶段通常无害，low-byte 还原阶段暴露真实语义，最终 sink 阶段把它当成路径、Header、JSON、SQL 或 HTML 执行。</p></div><div class=\"pipeline-grid\"><article><span>1</span><h3>原始输入</h3><p>WAF、黑名单和人工检查多数只看到这一层。</p></article><article><span>2</span><h3>Low-Byte 还原</h3><p><code>(byte) ch</code>、<code>writeBytes</code>、宽松解码会丢掉高位，恢复危险字节。</p></article><article><span>3</span><h3>最终 Sink</h3><p>路径、Header、JSON、SQL、HTML 在这一层获得真实攻击含义。</p></article></div></section>"
                + "<section class=\"group\" id=\"lab\"><div class=\"group-head\"><p class=\"group-tag\">Foundations</p><h2>基础对照</h2><p>先确认哪些字符会在 low-byte 阶段变成危险字节，再进入复杂利用链。</p></div><section class=\"grid\">"
                + card("1. 低字节视图", "输入任意字符串，看 Unicode 视图和 low-byte 视图如何分离。比如 陪、阮、严、灵、瘍、瘊。", "sourceInput", "陪sp", "inspectSource()", "分析输入", "inspectResult")
                + card("2. 上传扩展名绕过", "校验时只看原始文件名，不含 .jsp 就放行；保存时错误地按低 8 位写文件名。`1.陪sp` 会落成 `1.jsp`。", "uploadInput", "1.陪sp", "runUpload()", "模拟上传", "uploadResult")
                + "</section></section>"
                + "<section class=\"group\"><div class=\"group-head\"><p class=\"group-tag\">Paths & Protocols</p><h2>路径与协议边界</h2><p>这些场景展示 low-byte 还原后如何改变路径语义、协议结构和文件读取结果。</p></div><section class=\"grid\">"
                + card("3. 路径变形 / 双重解析", "先做一次看似安全的路径检查，再在后续阶段把低字节折叠成 `.%u002e` 并继续解码。`阮严灵丰丰甲来/secret/flag.txt` 最终会指向 `../secret/flag.txt`。", "pathInput", "阮严灵丰丰甲来/secret/flag.txt", "runPath()", "模拟读取", "pathResult")
                + card("4. CRLF / Header 注入", "展示高位字符在协议边界变成 `\\r\\n` 后，如何改写 HTTP 头结构。`token瘍瘊X-Evil: yes` 会被拆成两行。", "headerInput", "token瘍瘊X-Evil: yes", "runHeader()", "模拟写头", "headerResult")
                + card("7. 多段幽灵路径到 /etc/passwd", "更贴近你图片里的场景：多段 `阮严灵丰丰甲来/` 先折叠成 `.%u002e/`，再经 `%u002e -> .` 与 `%64 -> d` 的两次解码，最终从伪装路径还原到 `/etc/passwd`。", "fileReadInput", absoluteReadPayload, "runFileRead()", "模拟文件读取", "fileReadResult")
                + "</section></section>"
                + "<section class=\"group\"><div class=\"group-head\"><p class=\"group-tag\">Parsers & Sinks</p><h2>解析器与业务 Sink</h2><p>这些场景更接近真实业务，同一份原始输入在解析器和下游 sink 里会获得完全不同的安全语义。</p></div><section class=\"grid\">"
                + card("5. JSON / Fastjson @type", "先看原始 JSON 里并没有 ASCII `@type`，再看错误 low-byte 转换后如何还原成真正的 `@type`，并被 Fastjson 解析成 `java.awt.Rectangle`。", "jsonInput", escapeHtmlAttribute(autoTypePayload), "runJsonAutoType()", "模拟 JSON 解析", "jsonResult")
                + card("6. JSON 字段绕过到业务语义", "模拟接口在检查阶段只盯原始文本，随后把 low-byte JSON 交给解析器。原文里看不到 `root@localhost`，解析后业务层却拿到了真实用户名。", "userJsonInput", escapeHtmlAttribute(usernamePayload), "runJsonUser()", "模拟业务解析", "userJsonResult")
                + card("8. Ghost Bits -> SQLi", "检查阶段原文里看不到 ASCII `union select`，但 low-byte 还原后变成真实 SQL 片段，并被拼接进查询语句。", "sqliInput", escapeHtmlAttribute(sqlPayload), "runSqli()", "模拟 SQL 注入", "sqliResult")
                + card("9. Ghost Bits -> XSS", "原始输入不是普通 `<script>`，但 low-byte 还原后变成真实 HTML/JS 标签，再进入不转义的响应 sink。", "xssInput", escapeHtmlAttribute(xssPayload), "runXss()", "模拟 XSS", "xssResult")
                + "</section></section><section class=\"tips\"><h2>推荐输入</h2><ul><li><code>陪</code> -> low byte <code>0x6a</code> -> <code>j</code></li><li><code>阮严灵丰丰甲来</code> -> <code>.%u002e</code></li><li><code>瘍瘊</code> -> <code>\\r\\n</code></li><li><code>㹣౬ᙡ⑳⑳</code> -> <code>class</code></li><li><code>丱丠乵乮...</code> -> <code>1 union select ...</code></li><li><code>丼乳乣乲...</code> -> <code>&lt;script&gt;...&lt;/script&gt;</code></li></ul></section></main>"
                + "<script>"
                + script("sourceInput", "/api/inspect?input=", "inspectResult")
                + "async function runUpload(){await loadText('/api/upload-sink?filename='+encodeURIComponent(valueOf('uploadInput')),'uploadResult');}"
                + "async function runPath(){await loadText('/api/path-sink?path='+encodeURIComponent(valueOf('pathInput')),'pathResult');}"
                + "async function runHeader(){await loadText('/api/header-sink?value='+encodeURIComponent(valueOf('headerInput')),'headerResult');}"
                + "async function runJsonAutoType(){await loadText('/api/json-autotype?payload='+encodeURIComponent(valueOf('jsonInput')),'jsonResult');}"
                + "async function runJsonUser(){await loadText('/api/json-user?payload='+encodeURIComponent(valueOf('userJsonInput')),'userJsonResult');}"
                + "async function runFileRead(){await loadText('/api/file-read-sink?path='+encodeURIComponent(valueOf('fileReadInput')),'fileReadResult');}"
                + "async function runSqli(){await loadText('/api/sqli-sink?input='+encodeURIComponent(valueOf('sqliInput')),'sqliResult');}"
                + "async function runXss(){await loadText('/api/xss-sink?input='+encodeURIComponent(valueOf('xssInput')),'xssResult');}"
                + "async function inspectSource(){await loadText('/api/inspect?input='+encodeURIComponent(valueOf('sourceInput')),'inspectResult');}"
                + "function valueOf(id){return document.getElementById(id).value;}"
                + "async function loadText(url,targetId){const box=document.getElementById(targetId);box.textContent='请求处理中...';try{const r=await fetch(url);box.textContent='HTTP '+r.status+'\\n'+await r.text();}catch(e){box.textContent='请求失败: '+e;}}"
                + "</script></body></html>";
    }

    @GetMapping(value = "/api/inspect", produces = MediaType.TEXT_PLAIN_VALUE)
    public String inspect(@RequestParam String input) {
        return describe(input);
    }

    @GetMapping(value = "/api/upload-sink", produces = MediaType.TEXT_PLAIN_VALUE)
    public String uploadSink(@RequestParam String filename) throws IOException {
        boolean validatorAllows = !filename.toLowerCase().contains(".jsp");
        String storedName = lowByteString(filename);
        Path target = uploadRoot.resolve(storedName).normalize();
        Files.createDirectories(target.getParent());
        Files.write(target, ("ghost-bits upload demo: " + storedName + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return "validator_allows=" + validatorAllows
                + "\noriginal_filename=" + escapeVisible(filename)
                + "\nstored_filename=" + escapeVisible(storedName)
                + "\nstored_path=" + target
                + "\nlow_byte_report=\n" + describe(filename);
    }

    @GetMapping(value = "/api/path-sink", produces = MediaType.TEXT_PLAIN_VALUE)
    public String pathSink(@RequestParam("path") String userPath) throws IOException {
        boolean validatorAllows = !containsTraversal(userPath);
        String lowByte = lowByteString(userPath);
        String decoded = decodePercentU(lowByte);
        Path resolved = publicRoot.resolve(decoded).normalize();
        boolean escaped = !resolved.startsWith(publicRoot);
        String content = Files.exists(resolved) ? new String(Files.readAllBytes(resolved), StandardCharsets.UTF_8) : "<missing>";

        return "validator_allows=" + validatorAllows
                + "\noriginal_path=" + escapeVisible(userPath)
                + "\nlow_byte_path=" + escapeVisible(lowByte)
                + "\ndecoded_path=" + escapeVisible(decoded)
                + "\nresolved_path=" + resolved
                + "\nescaped_public_root=" + escaped
                + "\nfile_content=" + escapeVisible(content)
                + "\nlow_byte_report=\n" + describe(userPath);
    }

    @GetMapping(value = "/api/header-sink", produces = MediaType.TEXT_PLAIN_VALUE)
    public String headerSink(@RequestParam String value) {
        String rawLine = "X-Debug: " + lowByteString(value);
        byte[] bytes = lowByteBytes("X-Debug: " + value);
        List<String> lines = splitHeaderLines(rawLine);
        return "original_value=" + escapeVisible(value)
                + "\nraw_header_line=" + escapeVisible(rawLine)
                + "\nraw_header_hex=" + bytesToHex(bytes)
                + "\nparsed_lines=" + lines
                + "\nlow_byte_report=\n" + describe(value);
    }

    @GetMapping(value = "/api/json-autotype", produces = MediaType.TEXT_PLAIN_VALUE)
    public String jsonAutotype(@RequestParam String payload) {
        String lowBytePayload = lowByteString(payload);
        boolean rawContainsAutoType = payload.contains("@type");
        boolean lowByteContainsAutoType = lowBytePayload.contains("@type");
        try {
            Object parsed = JSON.parse(lowBytePayload);
            return "raw_contains_at_type=" + rawContainsAutoType
                    + "\nlow_byte_contains_at_type=" + lowByteContainsAutoType
                    + "\noriginal_payload=" + escapeVisible(payload)
                    + "\nlow_byte_payload=" + escapeVisible(lowBytePayload)
                    + "\nparsed_class=" + parsed.getClass().getName()
                    + "\nparsed_value=" + parsed
                    + "\nlow_byte_report=\n" + describe(payload);
        } catch (RuntimeException e) {
            return "raw_contains_at_type=" + rawContainsAutoType
                    + "\nlow_byte_contains_at_type=" + lowByteContainsAutoType
                    + "\noriginal_payload=" + escapeVisible(payload)
                    + "\nlow_byte_payload=" + escapeVisible(lowBytePayload)
                    + "\nparse_error=" + e.getClass().getName() + ": " + e.getMessage()
                    + "\nlow_byte_report=\n" + describe(payload);
        }
    }

    @GetMapping(value = "/api/json-user", produces = MediaType.TEXT_PLAIN_VALUE)
    public String jsonUser(@RequestParam String payload) {
        String lowBytePayload = lowByteString(payload);
        boolean validatorAllows = !payload.contains("root@localhost");
        JSONObject jsonObject = JSON.parseObject(lowBytePayload);
        String username = jsonObject.getString("username");
        boolean resolvedDangerousUser = "root@localhost".equals(username);
        return "validator_allows=" + validatorAllows
                + "\noriginal_payload=" + escapeVisible(payload)
                + "\nlow_byte_payload=" + escapeVisible(lowBytePayload)
                + "\nparsed_username=" + escapeVisible(username)
                + "\nresolved_dangerous_user=" + resolvedDangerousUser
                + "\nmock_sink_result=lookup(" + escapeVisible(username) + ")"
                + "\nlow_byte_report=\n" + describe(payload);
    }

    @GetMapping(value = "/api/file-read-sink", produces = MediaType.TEXT_PLAIN_VALUE)
    public String fileReadSink(@RequestParam("path") String userPath) throws IOException {
        boolean validatorAllows = !containsTraversal(userPath);
        String lowByte = lowByteString(userPath);
        String percentUDecoded = decodePercentU(lowByte);
        String fullyDecoded = decodePercentAscii(percentUDecoded);
        Path resolved = publicRoot.resolve(fullyDecoded).normalize();
        boolean escaped = !resolved.startsWith(publicRoot);
        boolean exists = Files.exists(resolved);
        String contentPreview = exists ? preview(resolved) : "<missing>";

        return "validator_allows=" + validatorAllows
                + "\noriginal_path=" + escapeVisible(userPath)
                + "\nlow_byte_path=" + escapeVisible(lowByte)
                + "\npercent_u_decoded=" + escapeVisible(percentUDecoded)
                + "\nfully_decoded_path=" + escapeVisible(fullyDecoded)
                + "\nresolved_path=" + resolved
                + "\nescaped_public_root=" + escaped
                + "\nfile_exists=" + exists
                + "\nfile_preview=" + escapeVisible(contentPreview)
                + "\nlow_byte_report=\n" + describe(userPath);
    }

    @GetMapping(value = "/api/sqli-sink", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sqliSink(@RequestParam("input") String input) {
        String lowByte = lowByteString(input);
        String normalized = lowByte.toLowerCase();
        boolean validatorAllows = !input.toLowerCase().contains("union select") && !input.contains("'");
        boolean dangerous = normalized.contains("union select") || normalized.contains(" or ") || lowByte.contains("'");
        String sql = "SELECT id, username FROM users WHERE username = '" + lowByte + "'";
        String mockResult = dangerous
                ? "[mock-db] low-byte payload changed query semantics and exposed extra rows"
                : "[mock-db] normal single-row lookup";
        return "validator_allows=" + validatorAllows
                + "\noriginal_input=" + escapeVisible(input)
                + "\nlow_byte_input=" + escapeVisible(lowByte)
                + "\ndangerous_sql_tokens_detected=" + dangerous
                + "\nconstructed_sql=" + escapeVisible(sql)
                + "\nmock_query_result=" + mockResult
                + "\nlow_byte_report=\n" + describe(input);
    }

    @GetMapping(value = "/api/xss-sink", produces = MediaType.TEXT_PLAIN_VALUE)
    public String xssSink(@RequestParam("input") String input) {
        String lowByte = lowByteString(input);
        String lower = lowByte.toLowerCase();
        boolean validatorAllows = !input.toLowerCase().contains("<script") && !input.toLowerCase().contains("onerror=");
        boolean dangerous = lower.contains("<script") || lower.contains("onerror=") || lower.contains("<img");
        String html = "<div class=\"comment\">" + lowByte + "</div>";
        return "validator_allows=" + validatorAllows
                + "\noriginal_input=" + escapeVisible(input)
                + "\nlow_byte_input=" + escapeVisible(lowByte)
                + "\ndangerous_html_tokens_detected=" + dangerous
                + "\nrendered_html=" + escapeVisible(html)
                + "\nwould_execute_in_browser=" + dangerous
                + "\nlow_byte_report=\n" + describe(input);
    }

    @GetMapping(value = "/api/status", produces = MediaType.TEXT_PLAIN_VALUE)
    public String status() throws IOException {
        List<String> uploads = new ArrayList<String>();
        if (Files.exists(uploadRoot)) {
            try (Stream<Path> stream = Files.list(uploadRoot)) {
                stream.forEach(path -> uploads.add(path.getFileName().toString()));
            }
        }
        return "lab_root=" + labRoot
                + "\nuploads=" + uploads
                + "\npublic_hello_exists=" + Files.exists(publicRoot.resolve("hello.txt"))
                + "\nsecret_flag_exists=" + Files.exists(secretRoot.resolve("flag.txt"));
    }

    private boolean containsTraversal(String path) {
        return path.contains("../") || path.contains("..\\") || path.contains("%2e") || path.contains("%2E");
    }

    private String decodePercentU(String input) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < input.length(); ) {
            if (i + 5 < input.length() && input.charAt(i) == '%' && input.charAt(i + 1) == 'u') {
                String hex = input.substring(i + 2, i + 6);
                if (isAsciiHex(hex)) {
                    out.append((char) Integer.parseInt(hex, 16));
                    i += 6;
                    continue;
                }
            }
            out.append(input.charAt(i));
            i++;
        }
        return out.toString();
    }

    private boolean isAsciiHex(String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            boolean digit = c >= '0' && c <= '9';
            boolean lower = c >= 'a' && c <= 'f';
            boolean upper = c >= 'A' && c <= 'F';
            if (!digit && !lower && !upper) {
                return false;
            }
        }
        return true;
    }

    private String decodePercentAscii(String input) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < input.length(); ) {
            if (i + 2 < input.length() && input.charAt(i) == '%') {
                String hex = input.substring(i + 1, i + 3);
                if (isAsciiHex(hex)) {
                    out.append((char) Integer.parseInt(hex, 16));
                    i += 3;
                    continue;
                }
            }
            out.append(input.charAt(i));
            i++;
        }
        return out.toString();
    }

    private List<String> splitHeaderLines(String raw) {
        List<String> lines = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '\r') {
                continue;
            }
            if (c == '\n') {
                lines.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        lines.add(current.toString());
        return lines;
    }

    private String describe(String input) {
        StringBuilder sb = new StringBuilder();
        sb.append("original=").append(escapeVisible(input)).append('\n');
        sb.append("char_count=").append(input.length()).append('\n');
        sb.append("low_byte_string=").append(escapeVisible(lowByteString(input))).append('\n');
        sb.append("low_byte_hex=").append(bytesToHex(lowByteBytes(input))).append('\n');
        sb.append("mapping=").append('\n');
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int low = c & 0xff;
            sb.append("  [").append(i).append("] ")
                    .append(escapeVisible(String.valueOf(c)))
                    .append(" U+").append(String.format("%04X", (int) c))
                    .append(" -> 0x").append(String.format("%02X", low))
                    .append(" -> ").append(printableAscii(low))
                    .append('\n');
        }
        return sb.toString();
    }

    private byte[] lowByteBytes(String input) {
        byte[] bytes = new byte[input.length()];
        for (int i = 0; i < input.length(); i++) {
            bytes[i] = (byte) input.charAt(i);
        }
        return bytes;
    }

    private String lowByteString(String input) {
        return new String(lowByteBytes(input), StandardCharsets.ISO_8859_1);
    }

    private String ghostAscii(String ascii) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ascii.length(); i++) {
            char c = ascii.charAt(i);
            if (c <= 0x7f && c != '\r' && c != '\n') {
                sb.append((char) (0x4e00 | c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String ghostJson(String asciiJson) {
        return ghostAscii(asciiJson);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(String.format("%02X", bytes[i] & 0xff));
        }
        return sb.toString();
    }

    private String printableAscii(int value) {
        if (value == '\r') {
            return "\\r";
        }
        if (value == '\n') {
            return "\\n";
        }
        if (value >= 32 && value <= 126) {
            return "'" + (char) value + "'";
        }
        return "<0x" + String.format("%02X", value) + ">";
    }

    private String escapeVisible(String value) {
        return value.replace("\\", "\\\\")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private String preview(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        String text = new String(bytes, StandardCharsets.UTF_8);
        return text.length() > 600 ? text.substring(0, 600) : text;
    }

    private String escapeHtmlAttribute(String value) {
        return value.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String escapeHtmlText(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private void writeIfMissing(Path path, String content) throws IOException {
        if (!Files.exists(path)) {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
        }
    }

    private String card(String title, String desc, String inputId, String initialValue, String fn, String btn, String resultId) {
        return "<article class=\"card\"><h2>" + escapeHtmlText(title) + "</h2><p>" + escapeHtmlText(desc) + "</p><label for=\"" + inputId + "\">输入</label><input id=\"" + inputId + "\" value=\"" + initialValue + "\"/><button onclick=\"" + fn + "\">" + escapeHtmlText(btn) + "</button><pre id=\"" + resultId + "\">等待发送请求...</pre></article>";
    }

    private String script(String inputId, String url, String resultId) {
        return "async function autoLoad" + inputId + "(){await loadText('" + url + "'+encodeURIComponent(valueOf('" + inputId + "')),'" + resultId + "');}";
    }

    private String style() {
        return "<style>"
                + ":root{--bg:#09111f;--panel:#101a2c;--panel2:#15243d;--line:#30445f;--text:#e7eefb;--muted:#9fb0cb;--accent:#62d0ff;--accent2:#8ef7c9;--danger:#ff8d8d}"
                + "*{box-sizing:border-box}html,body{margin:0;padding:0;background:radial-gradient(circle at top,#173055 0,#09111f 45%,#050914 100%);color:var(--text);font-family:Menlo,Monaco,Consolas,'Liberation Mono',monospace}"
                + "body{padding:28px}a{color:var(--accent);text-decoration:none}main.shell{max-width:1320px;margin:0 auto}section.hero{padding:32px;border:1px solid rgba(98,208,255,.22);background:linear-gradient(135deg,rgba(98,208,255,.08),rgba(142,247,201,.04));border-radius:24px;box-shadow:0 20px 60px rgba(0,0,0,.35)}.eyebrow{letter-spacing:.16em;text-transform:uppercase;color:var(--accent2);font-size:12px}.hero h1{font-size:52px;line-height:1.04;margin:10px 0 16px}.lead{max-width:920px;line-height:1.8;color:var(--muted)}.hero-actions{display:flex;gap:16px;flex-wrap:wrap;margin-top:22px}.hero-actions a{padding:12px 18px;border-radius:999px;border:1px solid rgba(98,208,255,.3);background:rgba(10,16,30,.55)}"
                + "section.pipeline{margin-top:22px;padding:22px;border:1px solid rgba(142,247,201,.18);background:linear-gradient(180deg,rgba(8,17,31,.95),rgba(13,24,40,.96));border-radius:22px}.pipeline-title h2,.group-head h2,.tips h2{margin:0 0 10px}.pipeline-title p,.group-head p{margin:0;color:var(--muted);line-height:1.75}.pipeline-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:14px;margin-top:18px}.pipeline-grid article{padding:18px;border-radius:18px;background:rgba(16,26,44,.95);border:1px solid rgba(98,208,255,.16)}.pipeline-grid span{display:inline-flex;width:34px;height:34px;border-radius:999px;align-items:center;justify-content:center;background:rgba(98,208,255,.16);color:var(--accent2);font-weight:700;margin-bottom:10px}.pipeline-grid h3{margin:0 0 8px;font-size:18px}.pipeline-grid p{margin:0;color:var(--muted);line-height:1.7}"
                + "section.group{margin-top:22px}.group-head{margin-bottom:14px;padding:0 2px}.group-tag{margin:0 0 8px;color:var(--accent2);letter-spacing:.12em;text-transform:uppercase;font-size:12px}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(290px,1fr));gap:18px}.card{background:linear-gradient(180deg,rgba(16,26,44,.98),rgba(12,20,34,.98));border:1px solid var(--line);border-radius:22px;padding:20px;min-height:430px}.card h2{margin:0 0 12px;font-size:22px}.card p{color:var(--muted);line-height:1.75;min-height:116px}.card label,.tips h2{display:block;margin:10px 0 8px;color:#d7e4fb;font-size:13px;text-transform:uppercase;letter-spacing:.08em}"
                + "input,pre,button{width:100%}input{background:#09111f;color:var(--text);border:1px solid #36506f;border-radius:14px;padding:12px 14px}button{margin-top:12px;border:0;border-radius:14px;padding:12px 14px;background:linear-gradient(90deg,var(--accent),#86a8ff);color:#07111f;font-weight:700;cursor:pointer}pre{margin-top:12px;background:#050914;border:1px solid #21334c;border-radius:14px;padding:14px;min-height:168px;overflow:auto;white-space:pre-wrap;word-break:break-word;color:#d8f7ff}.tips{margin-top:22px;background:rgba(16,26,44,.88);border:1px solid var(--line);border-radius:22px;padding:20px}.tips ul{margin:0;padding-left:20px;color:var(--muted);line-height:1.9}.tips code{color:var(--accent2)}"
                + "@media (max-width:720px){body{padding:14px}.hero h1{font-size:34px}.card p{min-height:auto}}"
                + "</style>";
    }
}
