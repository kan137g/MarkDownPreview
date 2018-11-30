package com.wc.markdownpreview;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.node.Visitor;
import org.commonmark.parser.Parser;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.renderer.SpannableMarkdownVisitor;
import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.tasklist.TaskListExtension;

/**
 * 预览MarkDown的文件，只支持标准标签，目前不支持下载
 */
public class MainActivity extends AppCompatActivity {

  private TextView textView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    textView = findViewById(R.id.text_view);

    String mdResource = openFile("android framework.md");
    CharSequence text = initMDTools(mdResource);
    // 显示
    textView.setText(text);
    // 设置让TextView也能滚动
    textView.setMovementMethod(ScrollingMovementMethod.getInstance());

  }

  /**
   * 初始化工具
   * @param markdown md文件的内容
   * @return
   */
  private CharSequence initMDTools(String markdown) {
    // 获取一个分析器
    final Parser parser = new Parser.Builder()
        // 注册所有已知标签 we will register all known to Markwon extensions
        .extensions(Arrays.asList(StrikethroughExtension.create(), TablesExtension.create(),
            TaskListExtension.create())).build();

      // 将md文件解析成标签
    final Node node = parser.parse(markdown);
    // spanner建造者，负责将xml标签解析为textView可显示的标签，如果做过html内容加载的话对这个会了解的多一点
    final SpannableBuilder builder = new SpannableBuilder();

    // 配置文字样式
    final float[] textSizeMultipliers = new float[] { 3f, 2f, 1.5f, 1f, .5f, .25f };
    // spanner配置
    SpannableConfiguration configuration = SpannableConfiguration.builder(this)
        .theme(SpannableTheme.builder()
            .headingTypeface(Typeface.MONOSPACE)
            .headingTextSizeMultipliers(textSizeMultipliers)
            .build())
        .build();

    Visitor visitor = new SpannableMarkdownVisitor(configuration, builder);

    node.accept(visitor);
    return builder.text();
  }

  /**
   * asset中的文件名，现在只支持这个，其他本地文件自己去封装一下
   * @param name asset中的文件名，现在只支持这个，其他本地文件自己去封装一下
   * @return
   */
  public String openFile(String name) {
    if (URLUtil.isNetworkUrl(name)) {
      Toast.makeText(this, "暂不支持网络文件", Toast.LENGTH_SHORT).show();
      return null;
    }
    String result;
    try {
      InputStream inputStream = getAssets().open(name);

      int size = inputStream.available();
      int len = -1;
      byte[] bytes = new byte[size];
      inputStream.read(bytes);
      inputStream.close();
      result = new String(bytes);
    } catch (IOException e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }
}
