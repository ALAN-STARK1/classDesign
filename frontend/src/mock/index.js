import { registerAdminMocks } from './modules/admin.mock'
import { registerAiMocks } from './modules/ai.mock'
import { registerAuthMocks } from './modules/auth.mock'
import { registerCommunityMocks } from './modules/community.mock'
import { registerHealthMocks } from './modules/health.mock'
import { registerMealMocks } from './modules/meal.mock'
import { registerNutritionMocks } from './modules/nutrition.mock'
import { registerRecipeMocks } from './modules/recipe.mock'

export function setupMock() {
  if (import.meta.env.VITE_USE_MOCK !== 'true') return
  registerAuthMocks()
  registerHealthMocks()
  registerRecipeMocks()
  registerMealMocks()
  registerNutritionMocks()
  registerAiMocks()
  registerCommunityMocks()
  registerAdminMocks()
}
