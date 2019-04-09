package com.Local_aplication.managers;

import com.Local_aplication.common.init;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class HTMLManager {
    private String path;
    private String output_file_path;

    public HTMLManager(String path, String output_file_path) {
        this.path = path;
        this.output_file_path = output_file_path;
    }

    public void build_html_file() {
        List<String> table_lines = get_table_lines_from_file();
        String body = build_html_table(table_lines);
        String html_text = build_html_string(body);
        write_text_to_file(html_text);
    }

    private String build_html_string(String body) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>Summary File</title>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "  border: 1px solid black;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h2>Summary File</h2>\n" +
                body +
                "</body>\n" +
                "</html>\n";
    }

    private String build_html_table(List<String> table_lines) {
        StringBuilder s = new StringBuilder()
                .append("<table style=\"width:100%\">\n")
                .append("<tr>\n")
                .append("    <th>Type</th>\n")
                .append("    <th>Old-URL</th> \n")
                .append("    <th>Result</th>\n")
                .append("  </tr>");
        for (String line : table_lines) {
            s.append(line);
        }
        s.append("</table>\n");
        return s.toString();
    }

    private String build_table_line(String type, String old_url, String new_url) {
        StringBuilder s = new StringBuilder()
                .append("<tr>\n")
                .append(String.format("<td style=\"text-align:center;\">%s</td>\n", type))
                .append(String.format("<td style=\"text-align:center;\"><a href=\"%s\">%s</a></td>\n", old_url, old_url));
        if (new_url.startsWith("ERROR"))
            s.append(String.format("<td style=\"color:rgb(255, 0, 0);text-align:center;\">%s</td>\n", new_url));
        else
            s.append(String.format("<td style=\"text-align:center;\"> <a href=\"%s\">%s</a></td>", new_url, new_url));
        return s.toString();
    }

    private List<String> get_table_lines_from_file() {
        List<String> table_lines = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            while (line != null) {
                parse_line_and_add_it_to_table_list(line, table_lines);
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            init.logger.log(Level.SEVERE, "an exception was thrown" + e.getMessage() + e.getCause() + Arrays.toString(e.getStackTrace()), e);
        }
        return table_lines;
    }

    private void parse_line_and_add_it_to_table_list(String line, List<String> list) {
        String[] splitted_line = line.split("\t");
        String type = splitted_line[0];
        String old_url = splitted_line[1];
        String new_url = splitted_line[2];
        String table_line = build_table_line(type, old_url, new_url);
        list.add(table_line);
    }

    private void write_text_to_file(String content) {
        try (PrintWriter writer = new PrintWriter(String.format("%s.html", output_file_path), "UTF-8")) {
            writer.print(content);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
            init.logger.log(Level.SEVERE, "an exception was thrown" + e.getMessage() + e.getCause() + Arrays.toString(e.getStackTrace()), e);
        }
    }
}
