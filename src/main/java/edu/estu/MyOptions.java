package edu.estu;

import org.kohsuke.args4j.Argument;

public class MyOptions {
    @Argument(required = true,usage = "enter the url you want to")
    String url;

}
