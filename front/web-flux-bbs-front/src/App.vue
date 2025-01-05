<template>
  <div id="app">
    <header>
      <nav>
        <ul>
          <li><router-link to="/">게시물 목록</router-link></li>
          <li><router-link to="/create">새 글 작성</router-link></li>
        </ul>
      </nav>
    </header>

    <main>
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
export default {
  name: "App",
};
</script>

<style>
/* 간단한 스타일링 예시 */
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  text-align: center;
  margin: 0 auto;
  padding: 20px;
}

header nav ul {
  list-style-type: none;
  display: flex;
  justify-content: center;
  padding: 0;
}

header nav ul li {
  margin: 0 10px;
}

header nav ul li a {
  text-decoration: none;
  color: #42b983;
}

header nav ul li a.router-link-active {
  font-weight: bold;
  color: #2c3e50;
}
</style>