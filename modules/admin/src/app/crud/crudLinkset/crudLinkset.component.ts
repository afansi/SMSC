import { Component, ModuleWithProviders, NgModule } from '@angular/core';
import { TranslateService, TranslateModule } from 'ng2-translate/ng2-translate';
import { Router, ActivatedRoute } from '@angular/router';
import { CrudService } from '../crud.service';
import { Location, CommonModule } from '@angular/common';
import { GridService } from '../../services/grid.service';
import { ColumnDefsModel } from '../model/columnDefs';
import { CrudLevel } from '../model/crudLevel';
import { RouterOutletService } from '../../services/routerOutletService';
import { MdModule } from '../../md.module';
import { MdSelectModule } from '../../common/material/select/select.component';
import { GridPaginationModule } from '../directives/gridPagination/gridPagination.component';
import { DropdownModule, AlertModule } from 'ng2-bootstrap';
import { AgGridModule } from 'ag-grid-ng2';
import { LoadingGridModule } from '../../common/loadingGrid.component';

@Component({
    selector: 'crud-linkset',
    template: require('./crudLinkset.component.html'),
    styleUrls: [
        require('./crudLinkset.component.scss'),
        require('../common/grid.scss'),
        require('../common/style.scss')
    ],
    providers: []
})

export class CrudLinksetComponent {
    public resolveData: ColumnDefsModel = null;

    constructor(public translate: TranslateService,
                public crudService: CrudService,
                public router: Router,
                public route: ActivatedRoute,
                public location: Location,
                public gridService: GridService,
                public roService: RouterOutletService) {
    }

    ngOnInit() {
        this.resolveData = this.route.snapshot.data['linkset'];
        this.crudService.gridOptions.columnDefs = this.resolveData.grid;
        this.crudService.gridOptions.rowData = [];

        // adds additional columns
        this.crudService.addColumn(this.crudService.gridOptions);
    }

    back() {
        this.crudService.previousCrudLevel();
        this.location.back();
    }

    navigateToCreate() {
        this.crudService.setModel({});
        this.router.navigate([this.crudService.parentPath,
            'create', this.crudService.getLinkedClass()]);
    }

    navigateToDelete() {
        let id = this.crudService.getSelectedRID(this.crudService.gridOptions);

        this.router.navigate([this.crudService.parentPath, 'delete',
            id.join().replace(/\[|\]/gi, '')]);
    }

    addLink(gridOptions) {
        let className = this.crudService.getLinkedClass();
        let previousCrudLevel: CrudLevel = this.crudService.previousCrudLevel();
        this.crudService.model[previousCrudLevel.linksetProperty.name] = [];
        let params: any = previousCrudLevel.linksetProperty.data;

        return this.getLinkset(gridOptions, previousCrudLevel.linksetProperty.type, className)
            .then(linkSet => {
                params[previousCrudLevel.linksetProperty.name] = linkSet;

                if (this.roService.isPreviousRoute('CrudViewComponent')) {
                    this.crudService.updateRecord(params);
                    this.location.back();
                } else {
                    this.crudService.model = params;
                    this.location.back();
                }

            });
    }

    getLinkset(gridOptions, type, className) {
        let focusedRows = gridOptions.api.getSelectedRows();
        let result = [];

        return this.gridService.getTitleColumns(className)
            .then((columnName) => {
                for (let i = 0; i < focusedRows.length; i++) {
                    switch (type) {
                        case 'LINKSET':
                            result['_' + i] = focusedRows[i]['@rid'];

                            if (focusedRows[i].hasOwnProperty(columnName) &&
                                typeof columnName !== 'undefined') {
                                result.push(focusedRows[i][columnName]);
                            } else {
                                result.push(focusedRows[i]['@rid']);
                            }
                            break;
                        case 'LINK':
                            result[0] = focusedRows[i][columnName];
                            result['rid'] = focusedRows[i]['@rid'];
                            break;

                        default:
                            break;
                    }
                }

                result['type'] = type;

                return result;
            });
    }

}

@NgModule({
    imports: [
        CommonModule,
        MdSelectModule,
        MdModule.forRoot(),
        DropdownModule,
        TranslateModule,
        GridPaginationModule,
        AlertModule,
        AgGridModule,
        LoadingGridModule
    ],
    exports: [CrudLinksetComponent],
    declarations: [CrudLinksetComponent]
})
export class CrudLinksetModule {
    static forRoot(): ModuleWithProviders {
        return {
            ngModule: CrudLinksetModule,
            providers: []
        };
    }
}