    package com.example.tfg;

    public class Estadistica {

        private long likes,comments;

        /**
         * Constructor
         * @param likes
         * @param comments
         */
        public Estadistica(long likes, long comments) {
            this.likes = likes;
            this.comments = comments;
        }

        /**
         * Getter de likes
         * @return
         */
        public long getLikes() {
            return likes;
        }

        /**
         * Setter de likes
         * @param likes
         */
        public void setLikes(long likes) {
            this.likes = likes;
        }

        /**
         * Getter de comentarios
         * @return
         */
        public long getComments() {
            return comments;
        }

        /**
         * Setter de comentarios
         * @param comments
         */
        public void setComments(long comments) {
            this.comments = comments;
        }
    }
