/*
 *    Copyright (C) 2013 eXo Platform SAS.
 *
 *    This is free software; you can redistribute it and/or modify it
 *    under the terms of the GNU Lesser General Public License as
 *    published by the Free Software Foundation; either version 2.1 of
 *    the License, or (at your option) any later version.
 *
 *    This software is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this software; if not, write to the Free
 *    Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *    02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.dashboard.pig.scripts;

import com.codenvy.dashboard.pig.scripts.util.Event;
import com.codenvy.dashboard.pig.scripts.util.LogGenerator;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a>
 */
public class TestScriptActiveTestantCount extends BasePigTest
{
   @Test
   public void testEventFound1UseCase() throws Exception
   {
      List<Event> events = new ArrayList<Event>();
      events.add(Event.Builder.createTenantCreatedEvent("ws1", "user1").withDate("2010-10-01").build());
      events.add(Event.Builder.createTenantCreatedEvent("ws2", "user2").withDate("2010-10-02").build());
      events.add(Event.Builder.createTenantDestroyedEvent("ws2").withDate("2010-10-05").build());

      File log = LogGenerator.generateLog(events);

      executePigScript(ScriptType.ACTIVE_TENANT_COUNT, log, new String[][]{{Constants.DATE, "20101001"}, {Constants.TO_DATE, "20101005"}});

      FileObject fileObject = ScriptType.ACTIVE_TENANT_COUNT.createFileObject(BASE_DIR, 20101001, 20101005);

      Long value = (Long)fileObject.getValue();
      Assert.assertEquals(value, Long.valueOf(2));
   }
   
   @Test
   public void testEventFound2UseCase() throws Exception
   {
      List<Event> events = new ArrayList<Event>();
      events.add(Event.Builder.createTenantCreatedEvent("ws1", "user1").withDate("2010-10-01").build());
      events.add(Event.Builder.createProjectCreatedEvent("user2", "ws2", "session", "project").withDate("2010-10-02")
         .build());
      events.add(Event.Builder.createProjectCreatedEvent("user2", "ws3", "session", "project").withDate("2010-10-02")
         .build());
      events.add(Event.Builder.createUserCreatedEvent("userId", "hello@gmail").withDate("2010-10-03").build());
      events.add(Event.Builder.createProjectCreatedEvent("user2", "ws5", "session", "project").withDate("2010-10-11")
         .build());

      File log = LogGenerator.generateLog(events);

      executePigScript(ScriptType.ACTIVE_TENANT_COUNT, log, new String[][]{{Constants.DATE, "20101001"},
         {Constants.TO_DATE, "20101005"}});

      FileObject fileObject = ScriptType.ACTIVE_TENANT_COUNT.createFileObject(BASE_DIR, 20101001, 20101005);

      Long value = (Long)fileObject.getValue();
      Assert.assertEquals(value, Long.valueOf(3));
   }

   @Test
   public void testEventNotFoundStoredDefaultValue() throws Exception
   {
      List<Event> events = new ArrayList<Event>();
      events.add(Event.Builder.createTenantCreatedEvent("ws1", "user1").withDate("2010-10-01").build());

      File log = LogGenerator.generateLog(events);

      executePigScript(ScriptType.ACTIVE_TENANT_COUNT, log, new String[][]{{Constants.DATE, "20101002"},
         {Constants.TO_DATE, "20101005"}});

      FileObject fileObject = ScriptType.ACTIVE_TENANT_COUNT.createFileObject(BASE_DIR, 20101002, 20101005);

      Long value = (Long)fileObject.getValue();
      Assert.assertEquals(value, Long.valueOf(0));
   }

   @Test
   public void fileObjectShouldReturnCorrectValue() throws Exception
   {
      Tuple tuple = TupleFactory.getInstance().newTuple();
      tuple.append(20100203);
      tuple.append(20100204);
      tuple.append(1);

      FileObject fileObject = ScriptType.ACTIVE_TENANT_COUNT.createFileObject(BASE_DIR, tuple);

      Assert.assertNotNull(fileObject.getKeys().get(Constants.DATE));
      Assert.assertNotNull(fileObject.getKeys().get(Constants.TO_DATE));

      Iterator<String> iter = fileObject.getKeys().keySet().iterator();
      Assert.assertEquals(iter.next(), Constants.DATE);
      Assert.assertEquals(iter.next(), Constants.TO_DATE);

      Assert.assertEquals(fileObject.getTypeResult(), ScriptResultType.ACTIVE_ENTITY_COUNT);
      Assert.assertEquals(fileObject.getKeys().get(Constants.DATE), "20100203");
      Assert.assertEquals(fileObject.getKeys().get(Constants.TO_DATE), "20100204");
      Assert.assertEquals(fileObject.getValue(), 1L);

      File file = new File("target/active_tenant_count/2010/02/03/20100204/value");
      file.delete();

      Assert.assertFalse(file.exists());

      fileObject.store();

      Assert.assertTrue(file.exists());
   }
}
