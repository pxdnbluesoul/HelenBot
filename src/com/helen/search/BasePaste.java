package com.helen.search;

import java.time.ZonedDateTime;

/**
 * <p>Base of the paste, this encapsulates some fields that the clients doens't
 * need to interact, just consult, because these fields are created and updated by the API and
 * not fetched for the Pastebin.</p>
 *
 * @author kennedy
 */
public abstract class BasePaste {
    protected ZonedDateTime localPasteDate;
    protected ZonedDateTime localExpirationDate;


    protected BasePaste() {
    }
}
