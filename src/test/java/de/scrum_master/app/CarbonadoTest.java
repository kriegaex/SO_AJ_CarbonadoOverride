package de.scrum_master.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.amazon.carbonado.FetchException;
import com.amazon.carbonado.PersistException;
import com.amazon.carbonado.Repository;
import com.amazon.carbonado.RepositoryException;
import com.amazon.carbonado.Storage;
import com.amazon.carbonado.SupportException;
import com.amazon.carbonado.repo.map.MapRepositoryBuilder;

import de.scrum_master.app.StoredMessage;

public class CarbonadoTest {
  private Repository repo;
  private Storage<StoredMessage> storage;
  StoredMessage message;

  @Before
  public void setUp() throws Exception {
    repo = MapRepositoryBuilder.newRepository();
    storage = repo.storageFor(StoredMessage.class);
    message = storage.prepare();
  }

  @After
  public void tearDown() throws Exception {
    repo.close();
    repo = null;
    storage = null;
    message = null;
  }

  @Test
  public void canCreateRecord() throws FetchException, PersistException {
    assertEquals(storage.query().count(), 0);
    message.setID(1);
    message.setMessage("Hello Carbonado!");
    message.insert();
    assertEquals(storage.query().count(), 1);
    message = storage.prepare();
    message.setID(2);
    message.setMessage("Hello World!");
    message.insert();
    assertEquals(storage.query().count(), 2);
  }

  @Test
  public void canUpdateRecord() throws PersistException, FetchException {
    message.setID(1);
    message.setMessage("Hello Carbonado!");
    message.insert();
    assertEquals(storage.query().count(), 1);

    message.setMessage("Hello World!");
    message.update();
    assertEquals(storage.query().count(), 1);

    message = storage.prepare();
    message.setID(1);
    assertTrue(message.tryLoad());
    assertEquals(message.getID(), 1);
    assertEquals(message.getMessage(), "Hello World!");
  }

  @Test
  public void canDeleteRecord() throws PersistException, FetchException {
    message.setID(1);
    message.setMessage("Hello Carbonado!");
    message.insert();
    assertEquals(storage.query().count(), 1);

    message = storage.prepare();
    message.setID(1);
    message.delete();
    assertEquals(storage.query().count(), 0);
  }

  @Test
  public void aspectCreatesNonExistentRecord() throws SupportException, RepositoryException {
    message.setID(1);
    // Without the aspect this would be false
    assertTrue(message.tryLoad());
    assertEquals(message.getID(), 1);
    assertEquals(message.getMessage(), null);
  }

}
