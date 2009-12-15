package it.av.eatt.web.commons;


import java.io.Serializable;

public class ActivityPaging implements Serializable {
    private int firstResult;
    private int maxResults;
    private int page;

    /**
     * @param firstResult
     * @param maxResults
     */
    public ActivityPaging(int firstResult, int maxResults) {
        super();
        this.firstResult = firstResult;
        this.maxResults = maxResults;
        this.page = 0;
    }

    public ActivityPaging addNewPage() {
        this.setPage(this.getPage() + 1);
        int offset = this.getMaxResults() * this.getPage();
        this.setFirstResult(this.getFirstResult() + offset);
        return this;
    }

    /**
     * @return the firstResult
     */
    public int getFirstResult() {
        return firstResult;
    }

    /**
     * @param firstResult the firstResult to set
     */
    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    /**
     * @return the maxResults
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * @param maxResults the maxResults to set
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

}