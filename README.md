# BubbleBarrage
仿QQ音乐冒泡弹幕

 bubbleBarrage = new BubbleBarrage()
                .init(this, bubbleBarrageContainer, R.layout.layout_bubble_barrage_item)
                .setIntervalTime(1000)
                .setVisibleCount(3)
                .setItemMargin(20)
                .setOnBarrageLoadListener(new BubbleBarrage.OnBarrageLoadListener() {
                    @Override
                    public void loadBarrage(View view, List<String> data, int index) {
                        view.findViewById(R.id.iv_head).setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.test_header));
                        ((TextView) view.findViewById(R.id.tv_content)).setText(data.get(index));
                    }
                }).start(bubbles, 1000);
