package com.helen.database.data;

import com.helen.commands.CommandData;
import org.junit.Test;

import java.util.List;

import static com.helen.database.data.Pages.findUnderratedArticles;
import static com.helen.database.data.Pages.getUnused;
import static org.junit.Assert.assertNotNull;

public class PagesTest {

    @Test
    public void unused(){
        String[] testStrings = new String[]{".unused",
                ".unused -s 1",
                ".unused -s 2",
                ".unused -s 3",
                ".unused -s 4",
                ".unused -s 5",
                ".unused -s 1 -c",
                ".unused -s 2 -c",
                ".unused -s 3 -c",
                ".unused -s 4 -c",
                ".unused -s 5 -c",
                ".unused -s 1 -l",
                ".unused -s 2 -l",
                ".unused -s 3 -l",
                ".unused -s 4 -l",
                ".unused -s 5 -l"};
        for (String message : testStrings) {
            CommandData d = new CommandData("", "Drmagnus", "test", "blah", message);
            assertNotNull(getUnused(d));
        }
    }

    @Test
    public void underrated(){
        String[] testStrings = new String[]{
                ".ur",
                ".ur -t",
                ".ur -t -s",
                ".ur -t -s -g",
                ".ur -t -s -g -r10;30",
                ".ur -r20;40"
        };

        for (String message : testStrings) {
            System.out.println(message);
            CommandData d = new CommandData("", "Drmagnus", "test", "blah", message);
            List<Page> returnString = findUnderratedArticles(d);
            assertNotNull(returnString);
            returnString.forEach(p -> System.out.println(p.getPageAsLine()));
            System.out.println("=============================");
        }
    }
}
