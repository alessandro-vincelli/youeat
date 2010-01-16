/**
 * 
 */
package it.av.youeat.web.components;

import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;

/**
 * An {@Link AjaxFallbackDefaultDataTable} without the {Link {@link NoRecordsToolbar}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class RistoranteDataTable<T> extends DataTable<T> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param id component id
     * @param columns list of columns
     * @param dataProvider data provider
     * @param rowsPerPage number of rows per page
     */
    @SuppressWarnings("unchecked")
    public RistoranteDataTable(String id, final List<IColumn<T>> columns, ISortableDataProvider<T> dataProvider,
            int rowsPerPage) {
        this(id, columns.toArray((IColumn<T>[]) new IColumn[columns.size()]), dataProvider, rowsPerPage);
    }

    /**
     * Constructor
     * 
     * @param id component id
     * @param columns array of columns
     * @param dataProvider data provider
     * @param rowsPerPage number of rows per page
     */
    public RistoranteDataTable(String id, final IColumn<T>[] columns, ISortableDataProvider<T> dataProvider,
            int rowsPerPage) {
        super(id, columns, dataProvider, rowsPerPage);
        setOutputMarkupId(true);
        setVersioned(false);
        addTopToolbar(new AjaxNavigationToolbar(this));
        addTopToolbar(new AjaxFallbackHeadersToolbar(this, dataProvider));
    }

    @Override
    protected Item<T> newRowItem(String id, int index, IModel<T> model) {
        return new OddEvenItem<T>(id, index, model);
    }
}