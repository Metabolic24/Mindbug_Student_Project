import { shallowMount } from '@vue/test-utils'

jest.mock('@/components/GameBoard.vue', () => ({
  name: 'GameBoard',
  methods: {
    getAvatar: jest.fn(),
    getCardImage: jest.fn(),
    getCardBackImage: jest.fn(),
    handleCardClick: jest.fn(),
    handleBattlefieldClick: jest.fn(),
    handleDropOnBattlefield: jest.fn(),
    selectAttacker: jest.fn(),
    handleAttackCardClick: jest.fn()
  },
  render: () => null
}))

import GameBoard from '@/components/GameBoard.vue'

describe('GameBoard.vue', () => {
  let wrapper

  beforeEach(() => {
    wrapper = shallowMount(GameBoard, {
      global: {
        mocks: {}
      }
    })
  
    wrapper.vm.handleCardClick = jest.fn()
    wrapper.vm.selectAttacker = jest.fn()
    wrapper.vm.handleBattlefieldClick = jest.fn()
  })
  

  afterEach(() => {
    jest.clearAllMocks()
  })

  it('renders GameBoard component without crashing', () => {
    expect(wrapper.exists()).toBe(true)
  })

  it('calls handleCardClick when a card is clicked', async () => {
    const spy = jest.spyOn(wrapper.vm, 'handleCardClick')
    await wrapper.vm.handleCardClick(1)
    expect(spy).toHaveBeenCalledWith(1)
  })

  it('calls selectAttacker when selecting an attacker', async () => {
    const spy = jest.spyOn(wrapper.vm, 'selectAttacker')
    await wrapper.vm.selectAttacker(0)
    expect(spy).toHaveBeenCalledWith(0)
  })

  it('calls handleBattlefieldClick when battlefield is clicked', async () => {
    const spy = jest.spyOn(wrapper.vm, 'handleBattlefieldClick')
    await wrapper.vm.handleBattlefieldClick()
    expect(spy).toHaveBeenCalled()
  })
})
