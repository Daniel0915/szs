<template>
  <div class="container mt-4">
    <!-- Top Bar with Navigation and Title -->
    <div class="d-flex align-items-center justify-content-between top-bar">
      <div class="left-section d-flex align-items-center">
        <button @click="backPage" class="btn btn-outline-secondary">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-caret-left" viewBox="0 0 16 16">
            <path d="M10 12.796V3.204L4.519 8zm-.659.753-5.48-4.796a1 1 0 0 1 0-1.506l5.48-4.796A1 1 0 0 1 11 3.204v9.592a1 1 0 0 1-1.659.753"></path>
          </svg>
          뒤로
        </button>
      </div>
      <h1 class="text-center m-0 flex-grow-1 title">{{ title }}</h1>
      <span class="text-muted right-section d-flex align-items-center">
        <i class="pi pi-home" style="font-size: 2rem;"></i>
      </span>
    </div>
    <hr class="my-3" style="border-top: 2px solid #000;">

    <div class="card">
      <ProgressBar :value="progressBarNum"></ProgressBar>
    </div>

    <!-- Image and Text Section -->
    <div class="text-center my-4">
      <img :src="listContent[page - 1].img" alt="Map Illustration" class="img-fluid" style="max-width: 300px" />
    </div>

    <!-- Question Text -->
    <h3 class="text-center font-weight-bold mb-4">
      {{ listContent[page - 1].question }}
    </h3>

    <!-- Answer Options -->
    <div class="d-flex flex-column gap-3">
      <button @click="nextPage" class="btn btn-light text-left border" style="padding: 12px 20px">
        {{ listContent[page - 1].answer_1 }}
      </button>
      <button @click="nextPage" class="btn btn-light text-left border" style="padding: 12px 20px">
        {{ listContent[page - 1].answer_2 }}
      </button>
    </div>

  </div>
</template>

<script>
import ProgressBar from 'primevue/progressbar';
import ProgressSpinner from 'primevue/progressspinner';
import Button from 'primevue/button';
import Toast from 'primevue/toast';



export default {
  name: "SelectPage",
  components: {ProgressBar, Button, Toast, ProgressSpinner},
  data() {
    return {
      title: "멍플갱어 테스트",
      listContent: [
        {
          page: 1,
          img: "https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg",
          question: "질문 1",
          answer1: "답변1",
          answer1Score: 1,
          answer2: "답변2",
          answer2Score: 2,
        },
        {
          page: 2,
          img: "https://png.pngtree.com/thumb_back/fh260/background/20230609/pngtree-three-puppies-with-their-mouths-open-are-posing-for-a-photo-image_2902292.jpg",
          question: "질문 2",
          answer1: "답변1",
          answer1Score: 1,
          answer2: "답변2",
          answer2Score: 1,
        },
        {
          page: 3,
          img: "https://png.pngtree.com/png-vector/20230221/ourmid/pngtree-cute-dog-illustration-png-image_6612074.png",
          question: "질문 3",
          answer1: "답변1",
          answer1Score: 1,
          answer2: "답변2",
          answer2Score: 1,
        },
        {
          page: 4,
          img: "https://images.pexels.com/photos/1458926/pexels-photo-1458926.jpeg?cs=srgb&dl=pexels-poodles2doodles-1458926.jpg&fm=jpg",
          question: "질문 4",
          answer1: "답변1",
          answer1Score: 1,
          answer2: "답변2",
          answer2Score: 1,
        },
      ],
    };
  },
  created() {
  },
  mounted() {
  },
  watch: {
    // page가 변경될 때 로딩 상태를 true로 설정
    page() {
      this.loading = true;
    }
  },
  computed: {
    progressBarNum() {
      if (this.listContent.length == 0) {
        return 0;
      }
      return (100 * this.page) / this.listContent.length;
    },
  },
  methods: {
    nextPage() {
      if (this.page == this.listContent.length) {
        alert("마지막 페이지입니다.");
        return;
      }
      this.page++;
    },
    backPage() {
      if (this.page == 1) {
        alert("첫 페이지입니다.");
        return;
      }
      this.page--;
    },
  },
}
</script>

<style scoped>
.container {
  font-family: "Jua", sans-serif;
  font-weight: 400;
  font-style: normal;
  max-width: 800px; /* 모바일과 유사한 너비 */
  margin: 0 auto; /* 중앙 정렬 */
  padding-bottom: 100px; /* 하단부 여백 추가 */
}

.top-bar {
  position: relative;
}

.left-section {
  display: flex;
  align-items: center;
}

.left-section i {
  font-size: 20px;
  margin-right: 5px;
}

.title {
  font-size: 30px;
  font-weight: bold;
  margin: 0;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.right-section {
  font-size: 18px;
}

h1 {
  font-size: 22px;
  margin: 0;
  text-align: center; /* 중앙 정렬 */
}

@media (max-width: 576px) {
  /* 작은 화면에서의 스타일 조정 */
  .container {
    margin: 0 auto; /* 중앙 정렬 */
    padding-bottom: 100px; /* 하단부 여백 추가 */
  }

  .title {
    font-size: 20px;
  }

  .right-section {
    font-size: 16px;
  }

  .answer-button {
    font-size: 16px;
  }

  h1 {
    font-size: 18px;
    font-weight: bold;
    margin: 0;
    text-align: center; /* 중앙 정렬 */
  }

  .answer-button {
    font-size: 14px;
  }
}

</style>
