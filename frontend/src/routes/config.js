import Test from '@/components/Server'
import App from "@/views/App"
import NewMapCreate from "@/views/NewMapCreate";


const routes = [
    {
      name: 'home',
      path: '/',
      component: import('@/views/Home')
    },
    {
        name: 'test',
        path: '/test',
        component: Test
    },
    {
        name: 'app',
        path: '/app',
        component: App
    },
    {
        name: 'NewMapCreate',
        path: '/nmc',
        // component: import('@/views/NewMapCreate')
        component: NewMapCreate
    },
    {
        name: 'PointMark',
        path: '/pm',
        component: import('@/views/PointMark')
    },
    {
        name: 'TableHeight',
        path: '/th',
        component: import('@/views/TableHeight')
    },
    {
        name: 'VoiceRecognition',
        path: '/vr',
        component: import('@/views/VoiceRecognition')
    },
    {
        name: 'MoveControl',
        path: '/mc',
        component: import('@/views/MoveControl')
    },
    {
        name: 'FetchStuff',
        path: '/fs',
        component: import('@/views/FetchStuff')
    },
    {
        name: 'Factorize',
        path: '/fact',
        component: import('@/views/Factorize')
    },
    {
        name: 'SystemUpdate',
        path: '/su',
        component: import('@/views/SystemUpdate')
    }
]

export default routes