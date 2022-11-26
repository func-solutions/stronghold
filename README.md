# stronghold-api
Инструмент для мультисерверной синхронизации глобальных бустеров.

<h2>Как подключить?</h2>

<h3>Get Started with `gradle`</h3>

```groovy
repositories {
    mavenCentral()
    maven {
        url 'https://repo.c7x.dev/repository/maven-public/'
        credentials {
            username System.getenv("CRI_REPO_LOGIN")
            password System.getenv("CRI_REPO_PASSWORD")
        }
    }
}

dependencies {
  
    // необходимые зависимости
    implementation 'me.func:atlas-api:1.0.10'
    implementation 'me.func:visual-driver:3.2.8.RELEASE'
    
    // сама библиотека
    implementation 'me.func:stronghold:1.1.2.RELEASE'
}
```

<h2>Чего не хватает?</h2>

1. Хранения бустеров в БД (для отказоустойчивости)

<h2>Примеры</h2>

```kotlin
// Инициализируем инструмент
Stronghold.namespace("the-tower")

// Получаем активные бустеры
val boosters = Stronghold.boosters()

// Активация бустера
Stronghold.activateBoosters(

  BoosterGlobal.builder()
    .type("money") // тип бустера по которому они будут группироваться
    .owner(player) // указываем игрока (можно отдельно указать uuid и name)
    .duration(7, TimeUnit.HOURS) // продолжительность
    .multiplier(2.0) // множитель
    .maxStackable(10) // максимальное сложение бустеров
    .build()
)

// Применяем бустер
val value: Double = 100.0
val finalValue = value.withBoosters("money")

// Указать логику при благодарности
Stronghold.addThanksConsumer { owner, player ->

  owner?.sendMessage("Вам +деньги")
  player?.sendMessage("Вы поблагодарили игрока")
}
```
