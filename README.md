# booster-api
Инструмент для мультисерверной синхронизации бустеров.

```kotlin
// Инициализируем инструмент
Stronghold.namespace("the-tower")

// Получаем активные бустеры
val boosters = Stronghold.boosters()

// Активация бустера
Stronghold.activateBoosters(GlobalBooster.builder()
  .type("money")
  .owner(player.uuid)
  .duration(7, TimeUnit.HOURS)
  .multiplier(2.0)
  .stackable(false)
  .thanks((player, owner) -> {
    player.sendMessage("Молодец что поблагодарил!")
    owner?.giveMoney(10.0)
  }).build()
)

// Применяем бустер
val value: Double = 100.0
val finalValue = value.withBoosters("money")
```
