import request from '@/utils/request'

export const mailConfigApi = {
  get: () => request.get('/mailConfig/admin/get'),
  update: (data) => request.post('/mailConfig/admin/update', data)
}

export const mailTemplateApi = {
  loadDataList: () => request.post('/mailTemplate/admin/loadDataList'),
  getById: (templateId) => request.post('/mailTemplate/admin/getById', { templateId }),
  upsert: (data) => request.post('/mailTemplate/admin/upsert', data),
  remove: (templateId) => request.post('/mailTemplate/admin/delete', { templateId })
}

export const mailLogApi = {
  loadDataList: (query) => request.post('/mailLog/admin/loadDataList', query)
}

export const mailSendApi = {
  send: (data) => request.post('/mailSend/admin/send', data)
}
