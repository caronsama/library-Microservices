import { createRouter, createWebHistory } from 'vue-router'
import Layout from "../layout/Layout";

const routes = [
  {
    path: '/',
    name: 'Layout',
    redirect:"dashboard",
    component: Layout,
    children:[
      {
        path:'user',
        name:'user',
        component:() => import("@/views/User")
      },
      {
        path: 'book',
        name: 'book',
        component: () => import("@/views/Book")
      },
      {
        path: 'person',
        name: 'Person',
        component: () => import("@/views/Person")
      },
      {
        path: 'password',
        name: 'Password',
        component: () => import("@/views/Password")
      },
      {
        path: 'lendrecord',
        name: 'LendRecord',
        component: () => import("@/views/LendRecord")
      },
      {
        path:'dashboard',
        name:'Dashboard',
        component:() => import("@/views/Dashboard")
      },
      {
        path:'seatmassage',
        name:'SeatMassage',
        component:() => import("@/views/SeatMassage")
      },
      {
        path:'ordermessage',
        name:'OrderMessage',
        component:() => import("@/views/OrderMessage")
      },
      {
        path:'createorder',
        name:'CreateOrder',
        component:() => import("@/views/CreateOrder")
      }
      // ,
      // {
      //   path: 'bookwithuser',
      //   name: 'BookWithUser',
      //   component: () => import("@/views/BookWithUser")
      // }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import("@/views/Login")
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import("@/views/Register")
  },
  {
    path: '/forget',
    name: 'Forget',
    component: () => import("@/views/Forget")
  },
  {
    path: '/message',
    name: 'message',
    component: () => import("@/views/message")
  },


]

const router = createRouter({
  mode: 'hash',
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
