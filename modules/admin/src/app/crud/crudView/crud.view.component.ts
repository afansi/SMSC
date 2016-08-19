import { Component } from "@angular/core";
import { TranslatePipe, TranslateService } from "ng2-translate/ng2-translate";
import { AgGridNg2 } from "ag-grid-ng2/main";
import { GridOptions } from "ag-grid/main";
import { Router, ActivatedRoute } from "@angular/router";
import { CrudService } from "../crud.service";
import { CrudLinkset } from "../crudLinkset/crud.linkset.component";
import { AlertComponent } from 'ng2-bootstrap/ng2-bootstrap';
import { LoadingGrid } from "../../common/loadingGrid";

@Component({
    selector: 'crud-view',
    template: require('./crud.view.html'),
    styles: [
        require('./crud.view.scss'),
        require('../common/style.scss')
    ],
    providers: [],
    directives: [
        AgGridNg2,
        CrudLinkset,
        AlertComponent,
        LoadingGrid,
        CrudView
    ],
    pipes: [ TranslatePipe ]
})

export class CrudView {
    public gridOptions:GridOptions;
    public showLinksetView = false;

    constructor(public translate:TranslateService,
                public crudService:CrudService,
                public router:Router,
                public route:ActivatedRoute) {
    }

    ngOnInit() {
        this.crudService.className = this.route.parent.parent.routeConfig.data[ 'crudClass' ];
        this.crudService.parentPath = this.router.url;

        this.crudService.initializationGrid(this.crudService.getClassName(),
            (rowData) => {
                if (!rowData.length) {
                    this.crudService.isInfoMessage = true;
                    this.crudService.infoMessage = 'orientdb.noRows';
                }

                this.crudService.gridOptions.rowData = rowData;
                this.crudService.setRowData(rowData, this.crudService.gridOptions);
            },
            (columnDefs) => {
                this.crudService.gridOptions.columnDefs = columnDefs;
                this.crudService.addCheckboxSelection(columnDefs, this.crudService.gridOptions);
            });

        this.gridOptions = this.crudService.gridOptions;
    }

    goToCreate() {
        this.crudService.multiCrud.push({
            goto: 'form',
            className: this.crudService.getClassName()
        });

        this.router.navigateByUrl(this.crudService.parentPath + '/create');
    }

    back(addLinkset?:(value) => void) {
        if (this.crudService.multiCrud.length) {
            this.crudService.lastCrudElement = this.crudService.multiCrud.pop();

            if (addLinkset) {
                addLinkset(this.crudService.lastCrudElement)
            }

            this.crudService.linkedClass = this.crudService.lastCrudElement.linkedClass;

            if (this.crudService.lastCrudElement.goto === 'grid') {
                this.showLinksetView = true;
            } else if (this.crudService.lastCrudElement.goto === 'form') {
                this.goToCreate();
            }
        }
    }

    addLink(gridOptions) {
        let focusedRows = gridOptions.api.getSelectedRows();
        let params:any;
        let linkSet = [];

        for (let item = 0; item < focusedRows.length; item++) {
            linkSet.push(focusedRows[ item ].rid);
        }

        this.back((element) => {
            if (element.goto === 'grid') {
                params = element.data;
                params[ element.field ] = linkSet;
                this.crudService.updateRecord(params);
            } else if (element.goto === 'form') {
                this.crudService.lastCrudElement.model[ element.field ] = linkSet;

                for (let i in this.crudService.multileSelect) {
                    this.crudService.multileSelect[ i ].init();
                }
            }
        });
    }

    clickOnCell(event) {
        let columnDefs = event.colDef;

        if (columnDefs.type === 'LINKSET' ||
            columnDefs.type === 'LINK') {
            this.crudService.multiCrud.push({
                linkedClass: this.crudService.getClassName(),
                data: event.data,
                field: columnDefs.field,
                goto: 'grid'
            });

            this.crudService.linkedClass = columnDefs.linkedClass;
            this.showLinksetView = true;
        }
    }
}