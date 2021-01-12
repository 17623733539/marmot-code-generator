<style lang="less">
    @import "./process.less";
</style>
<template>
    <div class="search">
        <!-- 1. 导航栏 -->
        <Row>
            <Form ref="searchForm" :model="searchForm">
                <Row :gutter="16">
                    <Col span="8">
                        <Row>
                            <Col span="6">
                                <FormItem>
                                    流程名称
                                </FormItem>
                            </Col>
                            <Col span="18">
                                <FormItem prop="name">
                                    <i-input
                                            v-model="searchForm.name"
                                            class="queryFormInput"
                                            placeholder="请输入流程名称..."/>
                                </FormItem>
                            </Col>
                        </Row>
                    </Col>
                    <Col span="8">
                        <Row>
                            <Col span="6">
                                <FormItem>
                                    部署时间
                                </FormItem>
                            </Col>
                            <Col span="18">
                                <DatePicker
                                        v-model="selectDate"
                                        type="daterange"
                                        format="yyyy-MM-dd"
                                        clearable
                                        @on-change="selectDateRange"
                                        placeholder="选择起始时间"
                                        style="width: 200px"
                                ></DatePicker>
                            </Col>
                        </Row>
                    </Col>

                </Row>
                <Row>
                    <Col span="12">
                        <FormItem>
                            <Button type="primary" size="small" @click="refreshTable">
                                <i class="iconfont icon-refresh"></i>{{$t('common.btn.refresh')}}
                            </Button>
                            <Button type="primary" size="small" @click="handleModal(0)">
                                <i class="iconfont icon-xinzeng2"></i>{{$t('common.btn.add')}}
                            </Button>
                            <Button type="error" size="small"  @click="deleteRows">
                                <i class="iconfont icon-shanchu"></i>{{$t('common.btn.delete')}}
                            </Button>
                            <Button @click="deployFlow" type="primary" icon="md-cloud-upload">部署流程</Button>
                        </FormItem>
                    </Col>
                    <Col span="8" offset="4">
                        <FormItem style="float: right;">
                            <Button type="primary" size="small" @click="pageTableQuery">
                                <i class="iconfont icon-chaxun3"></i>{{$t('common.btn.query')}}
                            </Button>
                            <Button type="primary" size="small" @click="resetForm('searchForm')">
                                <i class="iconfont icon-zhongzhi4"></i>{{$t('common.btn.reset')}}
                            </Button>
                            <Button type="primary" size="small"  @click="exportData">
                                <i class="iconfont icon-Excel"></i>{{$t('common.btn.export')}}
                            </Button>
                        </FormItem>
                    </Col>
                </Row>
            </Form>
        </Row>
        <!-- 2. 表格展示 -->
        <Row>
            <Table border size="small"
                   :loading="loading"
                   @on-selection-change="selectionChangeTable"
                   @on-sort-change="sortChangeTable"
                   :columns="columns"
                   :data="dataObject" ref="table"  sortable="custom">
            </Table>
        </Row>
        <!-- 3. 分页 -->
        <Row style="margin-top:10px;">
            <Page :total="page.total"
                  :page-size="page.pageSize"
                  :current="page.pageNumber"
                  @on-change="pageOnChange"
                  @on-page-size-change="pageSizeChange"
                  style="float:right;" show-total show-sizer show-elevator></Page>
        </Row>
        <!--4.部署流程文件-->
        <process-deploy-handle
                :show="deployHandleModal"
                :data="deployData"
                :handleStatus="handleStatus"
                @onModalClose="deployModalClose"
                @onok="deployok"
                @onFail="deployFail">
        </process-deploy-handle>
        <!--查看流程文件-->

    </div>
</template>
<script>
    import {apiWorkFlowProcessPageList,apiWorkFlowProcessDelete} from '../../../api/static';
    import ProcessDeployHandle from './ProcessDeployHandle';
    export default {
        name:'WorkFlowProcess',
        components:{
            ProcessDeployHandle,
        },
        data(){
            return{
                // 查询用户表单
                searchForm: {
                    name:'',
                    processKey:'',
                    status:'',
                    sort: 'id', // 默认排序字段
                    order: 'desc', // 默认排序方式
                    startDate:'', // 起始时间
                    endDate:'' // 终止时间
                },
                modalVisible:'',
                deployData:{},
                handleStatus:false,
                deployHandleModal:false,
                selectDate:null,//绑定时间
                loading:false,
                selectIds:[],
                // 用户表格数据
                dataObject: [],
                selectItems:[],
                // 分页对象
                page: {
                    pageNumber: 1,
                    pageSize: 10,
                    total: 0,
                    sort: 'id', // 默认排序字段
                    order: 'desc', // 默认排序方式
                },
            }
        },
        computed:{
            columns() {
                return [

                ]
            }
        },
        methods:{
            init(){
                //加载数据表
                this.pageTableQuery();
            },

            deployFlow(){
                this.deployHandleModal=true;

            },
            deployModalClose(value){
                this.deployHandleModal=value;
            },
            deployok(){

            },
            deployFail(){

            },
            //时间选择器
            selectDateRange(v) {
                if (v) {
                    this.searchForm.startDate = v[0];
                    this.searchForm.endDate = v[1];
                }
            },


            //多行删除
            deleteRows(){
                if (this.selectIds.length === 0) {
                    this.$common.message(this.$common.WARNING,this.$t('message.deleteSelect'))
                    return;
                }
                this.deleteConfirm(dataVo);
            },
            //单行删除
            deleteRow(ids){
                this.deleteConfirm(ids);
            },
            //确认删除
            deleteConfirm(dataVo=new Array()){
                let reqVo = {list: dataVo};
                //删除确认对话框
                this.$Modal.confirm({
                    title: this.$t('message.deleteTitle'),
                    content: this.$t('message.deleteNotice'),
                    onOk: () => {
                        apiWorkFlowProcessDelete(reqVo).then(res => {
                            if (res.level == this.$common.ok) {
                                this.refreshTable();
                            }
                            this.$common.message(res.level, res.message);
                        })
                        // 清空删除数组
                        this.selectIds = [];
                    },
                    onCancel: () => {
                        this.$common.message(this.$common.WARNING, this.$t('message.deleteCancel'))
                    }
                })
            },
            handleModal(){

            },
            resetForm(){
                this.$refs[name].resetFields();
            },
            refreshTable(){
                this.pageTableQuery();
            },
            exportData(){

            },
            selectionChangeTable(items){
                this.selectIds = items.map(row =>{id:row.id});
                this.selectItems=items;
            },
            sortChangeTable(col){
                this.page.sort = col.key;
                this.page.order = col.order;
                if (col.order === 'normal') {
                    this.page.order ='';
                }
                this.pageTableQuery();
            },
            pageOnChange(pageNumber){
                this.page.pageNumber = pageNumber;
                this.pageTableQuery();
            },
            pageSizeChange(pageSize=10){
                this.page.pageSize = pageSize;
                this.pageTableQuery();
            },
            pageTableQuery(){
                this.query(this.page, this.searchForm);
            },
            query(page, reqVo = {}){
                page.pageNumber=(page.pageNumber-1)<0?0:page.pageNumber-1;
                reqVo['page']=page;
                //查询数据
                this.loading = true;
                apiWorkFlowProcessPageList(reqVo).then(res => {
                    this.loading = false;
                    this.dataObject = res.body.content;
                    this.page.total = res.body.totalElements;
                })
            }
        },
        mounted(){
            this.init();
        }
    }
</script>
