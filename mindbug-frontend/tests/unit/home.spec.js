import { shallowMount } from '@vue/test-utils'
import Home from '@/components/HomePage.vue'

jest.mock('@/services/websocket.js', () => ({
  connectToQueue: jest.fn()
}))

jest.mock('axios', () => ({
  post: jest.fn(() => Promise.resolve({ data: { playerId: '12345' } }))
}))

describe('Home.vue', () => {
  let wrapper
  let mockRouter

  beforeEach(() => {
    mockRouter = {
      push: jest.fn()
    }

    wrapper = shallowMount(Home, {
      global: {
        mocks: {
          $router: mockRouter
        }
      }
    })
  })

  afterEach(() => {
    jest.clearAllMocks()
  })

  it('calls goToSets when "Set of Cards" button is clicked', async () => {
    const setOfCardsButton = wrapper.find('button:nth-of-type(1)')
    await setOfCardsButton.trigger('click')
    expect(mockRouter.push).toHaveBeenCalledWith('/setsofcards')
  })

  it('calls startGame when "Start Game" button is clicked', async () => {
    window.alert = jest.fn()
    wrapper.vm.handleMatchFound = jest.fn()

    const startGameButton = wrapper.find('button:nth-of-type(2)')
    await startGameButton.trigger('click')

    const axios = require('axios')
    expect(axios.post).toHaveBeenCalledWith('http://localhost:8080/api/game/join_game')
    expect(wrapper.vm.playerId).toBe('12345')
  })
})
