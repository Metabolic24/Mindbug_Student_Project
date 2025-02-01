<template>
  <div id="home">
    <h1>Welcome to Mindbug's Cards</h1>
    <div class="button-group">
      <button @click="goToSets" class="styled-button">Set of Cards</button>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    goToSets() {
      this.$router.push('/setsofcards');
    }
  }
}
</script>

<style scoped>
#home {
  text-align: center;
  margin-top: 50px;
}

h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 40px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 20px; 
}

.styled-button {
  padding: 12px 30px;
  font-size: 18px;
  color: #fff;
  background-color: #3498db;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s, transform 0.2s;
}

.styled-button:hover {
  background-color: #2980b9;
  transform: scale(1.05); 
}

.styled-button:active {
  background-color: #1e6f93;
  transform: scale(0.98); 
}

</style>
