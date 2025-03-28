import { createRouter, createWebHistory } from 'vue-router'
import FileUpload from '../components/FileUpload.vue'
import DataVisualization from '../components/DataVisualization.vue'

const routes = [
  {
    path: '/',
    name: 'Upload',
    component: FileUpload
  },
  {
    path: '/data/:fileId',
    name: 'Visualization',
    component: DataVisualization,
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router