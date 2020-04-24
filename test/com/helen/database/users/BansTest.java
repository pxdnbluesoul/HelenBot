package com.helen.database.users;


import com.helen.commands.CommandData;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BansTest {


    @Before
    public void init() throws IOException {
        Bans.updateBans();
    }

    @Test
    public void testBasicBan() {
        System.out.println("Basic ban, should return gdfgd.");

        BanInfo info = Bans.getUserBan("gdfgd","","#site19","");
        assertNotNull(info);
        System.out.println(info);
    }

    @Test
    public void testLoginBan(){
        System.out.println("Ban with login, should return thecoded.");

        BanInfo info = Bans.getUserBan("steve","abcdefg","#site17","thecoded");
        assertNotNull(info);
        System.out.println(info);
    }

    @Test
    public void testIpBanWithWildcards(){
        System.out.println("Ban with wildcards, should return xeno.");
        BanInfo info = Bans.getUserBan("a","abcdefg.1909396C.863D485C.IP","#site19","");
        assertNotNull(info);
        System.out.println(info);


        System.out.println("Ban with wildcards 2, should return clockstop.");
        info = Bans.getUserBan("a","190.92.8.17","#site19","");
        assertNotNull(info);
        System.out.println(info);
    }

    @Test
    public void testMonthsForBan(){
        LocalDateTime now = LocalDateTime.now();
        CommandData data = CommandData.getTestData(".addban -u test|-d 1m|-r reason|-c #site19","#site19");
        BanPrep prep = new BanPrep(data);

        assertTrue(prep != null && prep.getEndTime().compareTo(now.plusMonths(1)) > 0);
    }
}
