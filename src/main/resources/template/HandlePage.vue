<template>
    <Modal v-model="showModal"
           @on-visible-change="onVisibleChange"
           :mask-closable="false">
        <p slot="header" style="text-align:left">
            <span v-if="handleStatus"><i class="iconfont" style="margin-right:3px;">&#xe64a;</i>{{$t('common.btn.add')}}</span>
            <span v-if="!handleStatus"><i class="iconfont" style="margin-right:3px;">&#xe659;</i>{{$t('common.btn.edit')}}</span>
        </p>
        <div>
            <Form ref="dataForm" :model="dataForm" :rules="rules" :label-width="120">
                <Row :gutter="16">
                    <i-col :span="20">
                        <FormItem :label="$t('auth.position.table.nameEn')" prop="nameEn">
                            <Input size="small" v-model="dataForm.nameEn" :placeholder="$t('auth.position.placeholder.nameEn')" :maxlength="128"></Input>
                        </FormItem>
                    </i-col>
                </Row>
                <Row :gutter="16">
                    <i-col :span="20">
                        <FormItem :label="$t('auth.position.table.nameZh')" prop="nameZh">
                            <Input size="small" v-model="dataForm.nameZh" :placeholder="$t('auth.position.placeholder.nameZh')" :maxlength="128"></Input>
                        </FormItem>
                    </i-col>
                </Row>
                <Row :gutter="16">
                    <i-col :span="20">
                        <FormItem :label="$t('auth.position.table.remark')" prop="remark">
                            <Input size="small" v-model="dataForm.remark" :placeholder="$t('auth.position.placeholder.remark')" :maxlength="128"></Input>
                        </FormItem>
                    </i-col>
                </Row>
            </Form>
        </div>
        <div slot="footer">
            <Row>
                <i-col :span="14">
                </i-col>
                <i-col span="8" offset="4" style="float: right;">
                    <Button type="primary" style="margin-right:10px;" class="ivu-btn ivu-btn-text" @click="handleCancel('dataForm')">
                        {{$t('common.btn.cancel')}}
                    </Button>
                    <Button type="primary" :loading="ok.loading"   @click="handleSubmit('dataForm')">
                        <span v-if="!ok.loading">{{$t('common.btn.confirm')}}</span>
                        <span v-else>{{$t('common.btn.confirm')}}</span>
                    </Button>
                </i-col>
            </Row>
        </div>
    </Modal>
</template>
<script>

    import {apiAuthPositionHandle} from '@/api/index.js'
    export default {
        name: "AuthPositionHandle",
        props: {
            // 接收传来的值，用来控制Modal的显示
            show: {
                type: Boolean,
                default: false
            },
            data: {
                type: Object,
            },
            handleStatus: {
                type: Boolean,
                default: true
            }
        },
        data() {
            return {
                // 初始化信息总条数
                dataCount: 0,
                // 每页显示多少条
                pageSize: 10,
                editable: this.$common.EDITABLE_STATUS,
                // 添加用户信息表单
                dataForm: {
                    nameEn: '',
                    nameZh: '',
                    remark: '',
                    editable: 1,
                },
                oldData:{},
                ok: {
                    loading: false
                },
            }
        },
        computed: {
            rules() {
                return {
                    nameEn: [
                        {required: true, message: this.$t('auth.position.table.nameEn')+this.$t('message.notNull'), trigger: 'blur'}
                    ],
                    nameZh: [
                        {required: true, message: this.$t('auth.position.table.nameZh')+this.$t('message.notNull'), trigger: 'blur'}
                    ],
                }
            },
            showModal: {
                set: function (value) {
                    return value;
                },
                get: function () {
                    return this.show;
                }
            }
        },
        watch: {
            data: {
                handler(newValue, oldValue) {
                    this.oldData = newValue;
                    this.handleData(this.oldData);
                },
                deep: true
            }
        },
        methods: {
            handleSubmit(name) {
                this.ok.loading = true;
                this.$refs[name].validate((valid) => {
                    if (valid) {
                        apiAuthPositionHandle(this.dataForm).then((res => {
                            //设置Btn定时关闭
                            this.disabledTimeBtn();
                            // 成功失败事件
                            if (res.level === this.$common.ok) {
                                this.$common.message(res.level, res.message);
                                this.$emit('onok');
                            } else {
                                this.$common.message(res.level, res.message);
                                this.$emit('onFail');
                            }
                            this.submitBtnStatus(false);
                        }), (error) => {
                            this.submitBtnStatus(false);
                        });
                    } else {
                        this.submitBtnStatus(false);
                    }
                });
            },
            submitBtnStatus(value) {
                this.ok.loading = value;
            },
            disabledTimeBtn(){
                setTimeout(()=>{
                    //设置不为不是加载中
                    this.submitBtnStatus(false);
                },6000);
            },
            handleCancel() {
                this.onVisibleChange(false);
            },
            onVisibleChange(value) {
                if (this.oldData) {
                    this.handleData(this.oldData);
                }
                if(!value){
                    this.$refs['dataForm'].resetFields();
                }
                this.$emit('onModalClose', value);
            },
            handleData(data) {
                this.$deepExtend(this.dataForm, data);
            }
        }

    }
</script>