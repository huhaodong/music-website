import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import YinIcon from './layouts/YinIcon.vue'

describe('YinIcon', () => {
  it('should render icon correctly', () => {
    const wrapper = mount(YinIcon, {
      props: {
        icon: '#icon-test'
      }
    })
    expect(wrapper.exists()).toBe(true)
  })

  it('should render svg element', () => {
    const wrapper = mount(YinIcon, {
      props: {
        icon: '#icon-test'
      }
    })
    const svg = wrapper.find('svg')
    expect(svg.exists()).toBe(true)
  })

  it('should apply icon class', () => {
    const wrapper = mount(YinIcon, {
      props: {
        icon: '#icon-test'
      }
    })
    expect(wrapper.classes()).toContain('icon')
  })

  it('should have aria-hidden attribute', () => {
    const wrapper = mount(YinIcon, {
      props: {
        icon: '#icon-test'
      }
    })
    const svg = wrapper.find('svg')
    expect(svg.attributes('aria-hidden')).toBe('true')
  })

  it('should update icon when prop changes', async () => {
    const wrapper = mount(YinIcon, {
      props: {
        icon: '#icon-test'
      }
    })
    await wrapper.setProps({ icon: '#icon-new' })
    const use = wrapper.find('use')
    expect(use.attributes('xlink:href')).toBe('#icon-new')
  })
})
